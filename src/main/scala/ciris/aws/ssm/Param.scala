package ciris.aws.ssm

import cats.effect.Blocker
import ciris.{ConfigKey, ConfigValue}
import com.amazonaws.services.simplesystemsmanagement._
import com.amazonaws.services.simplesystemsmanagement.model._

sealed abstract class Param {
  def apply(key: String): ConfigValue[String]
}

private[ssm] final object Param {
  final def apply(client: AWSSimpleSystemsManagement, blocker: Blocker): Param =
    new Param {
      override final def apply(key: String): ConfigValue[String] =
        ConfigValue.blockOn(blocker) {
          ConfigValue.suspend {
            val configKey =
              ConfigKey(s"parameter $key from AWS SSM")

            try {
              val result =
                client.getParameter {
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
            }
          }
        }
    }
}
