package ciris.aws

import ciris._
import ciris.api._
import com.amazonaws.auth._
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder

import scala.util.Try

package object ssm {

  val SecretString: ConfigKeyType[String] =
    ConfigKeyType[String]("secret string from AWS parameter store")

  def awsSsmSource(
      region: Regions,
      credsProvider: AWSCredentialsProvider
  ): ConfigSource[Id, String, String] =
    ConfigSource.fromTryOption(SecretString) { key =>
      val ssmClient = AWSSimpleSystemsManagementClientBuilder
        .standard()
        .withRegion(region)
        .withCredentials(credsProvider)
        .build()
      try {
        val request = new GetParameterRequest()
          .withName(key)
          .withWithDecryption(true)
        Try(ssmClient.getParameter(request)).map { result =>
          for {
            param <- Option(result.getParameter)
            value <- Option(param.getValue)
          } yield value
        }
      } finally {
        ssmClient.shutdown()
      }
    }

  /**
    * Reads the parameter with the specified key from AWS Simple Systems
    * Management's parameter store. If the parameter is a "secret string",
    * it will be decrypted.
    */
  def param[Value](key: String)(
      implicit region: Regions = Regions.EU_WEST_1,
      credsProvider: AWSCredentialsProvider =
        new DefaultAWSCredentialsProviderChain(),
      decoder: ConfigDecoder[String, Value]
  ): ConfigEntry[Id, String, String, Value] = {
    awsSsmSource(region, credsProvider)
      .read(key)
      .decodeValue[Value]
  }

  /**
    * Reads the parameter with the specified key from AWS Simple Systems
    * Management's parameter store, suspending the reading into `F`. If
    * the parameter is a "secret string", it will be decrypted.
    */
  def paramF[F[_]: Sync, Value](key: String)(
      implicit region: Regions = Regions.EU_WEST_1,
      credsProvider: AWSCredentialsProvider =
        new DefaultAWSCredentialsProviderChain(),
      decoder: ConfigDecoder[String, Value]
  ): ConfigEntry[F, String, String, Value] = {
    awsSsmSource(region, credsProvider)
      .suspendF[F]
      .read(key)
      .decodeValue[Value]
  }
}
