package ciris.aws.ssm

import ciris.{ConfigKey, ConfigValue}
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.{GetParameterRequest, ParameterNotFoundException}

sealed abstract class Param[F[_]] {
  def apply(key: String): ConfigValue[F, String]
}

private[ssm] object Param {
  final def apply[F[_]](client: SsmClient): Param[F] =
    new Param[F] {
      override final def apply(key: String): ConfigValue[F, String] =
        ConfigValue.blocking {
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
