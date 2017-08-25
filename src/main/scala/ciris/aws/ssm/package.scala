package ciris.aws

import ciris.{ConfigKeyType, ConfigReader, ConfigSource, ConfigValue}
import com.amazonaws.auth.{
  AWSCredentialsProvider,
  DefaultAWSCredentialsProviderChain
}
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder

import scala.util.Try

package object ssm {

  val SecretString: ConfigKeyType[String] =
    ConfigKeyType[String]("secret string from AWS parameter store")

  def awsSsmSource(
      region: Regions,
      credsProvider: AWSCredentialsProvider): ConfigSource[String] =
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
    * Read a parameter from AWS Simple Systems Management's parameter store.
    *
    * If the parameter is a "secret string", it will be decrypted.
    */
  def param[Value: ConfigReader](key: String)(
      implicit region: Regions = Regions.EU_WEST_1,
      credsProvider: AWSCredentialsProvider =
        new DefaultAWSCredentialsProviderChain): ConfigValue[Value] =
    ConfigValue(key)(awsSsmSource(region, credsProvider), ConfigReader[Value])
}
