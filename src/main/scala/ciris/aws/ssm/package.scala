package ciris.aws

import cats.effect.Blocker
import ciris._
import com.amazonaws.auth._
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement._
import com.amazonaws.services.simplesystemsmanagement.model._

package object ssm {
  final def param(key: String, blocker: Blocker)(
    implicit region: Regions = Regions.EU_WEST_1,
    credsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()
  ): ConfigValue[String] =
    ConfigValue.blockOn(blocker) {
      ConfigValue.suspend {
        val ssmClient =
          AWSSimpleSystemsManagementClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(credsProvider)
            .build()

        val configKey =
          ConfigKey(s"parameter $key from AWS SSM")

        try {
          val result =
            ssmClient.getParameter {
              new GetParameterRequest()
                .withWithDecryption(true)
                .withName(key)
            }

          val loaded =
            for {
              parameter <- Option(result.getParameter)
              value <- Option(parameter.getValue)
            } yield ConfigValue.loaded(configKey, value)

          loaded.getOrElse(ConfigValue.missing(configKey))
        } catch {
          case _: ParameterNotFoundException =>
            ConfigValue.missing(configKey)
        } finally {
          ssmClient.shutdown()
        }
      }
    }
}
