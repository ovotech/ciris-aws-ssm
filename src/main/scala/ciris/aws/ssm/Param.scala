package ciris.aws.ssm

import cats.effect.Blocker
import ciris.{ConfigKey, ConfigValue}
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.{GetParameterRequest, ParameterNotFoundException}

sealed abstract class Param {
  def apply(key: String): ConfigValue[String]
}

private[ssm] final object Param {
  final def apply(client: SsmClient, blocker: Blocker): Param =
    new Param {
      override final def apply(key: String): ConfigValue[String] =
        ConfigValue.blockOn(blocker) {
          ConfigValue.suspend {
            val configKey =
              ConfigKey(s"parameter $key from AWS SSM")

            try {
              val result =
                client.getParameter {
                  GetParameterRequest
                    .builder()
                    .withDecryption(true)
                    .name(key)
                    .build()
                }

              val loaded =
                for {
                  parameter <- Option(result.parameter())
                  value <- Option(parameter.value)
                } yield ConfigValue.loaded(configKey, value)

              loaded.getOrElse(ConfigValue.missing(configKey))
            } catch {
              case _: ParameterNotFoundException =>
                ConfigValue.missing(configKey)
            }
          }
        }
    }
}
