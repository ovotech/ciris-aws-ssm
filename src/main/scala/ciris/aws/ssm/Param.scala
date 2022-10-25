package ciris.aws.ssm

import cats.effect.Async
import cats.implicits.{catsSyntaxApplicativeError, toFunctorOps}
import ciris.{ConfigKey, ConfigValue, Effect}
import software.amazon.awssdk.services.ssm.model.{GetParameterRequest, GetParameterResponse, ParameterNotFoundException}
import software.amazon.awssdk.services.ssm.{SsmAsyncClient, SsmClient}

sealed abstract class Param[F[_]] {
  def apply(key: String): ConfigValue[F, String]
}

private[ssm] object Param {

  def buildKey: String => ConfigKey = key => ConfigKey(s"parameter $key from AWS SSM")

  def buildRequest: String => GetParameterRequest =
    key =>
      GetParameterRequest
        .builder()
        .withDecryption(true)
        .name(key)
        .build()

  def awsResponseToConfigValue(
    configKey: ConfigKey,
    result: GetParameterResponse
  ): ConfigValue[Effect, String] = {
    val loaded =
      for {
        parameter <- Option(result.parameter())
        value <- Option(parameter.value)
      } yield ConfigValue.loaded(configKey, value)

    loaded.getOrElse(ConfigValue.missing(configKey))
  }

  final def fromSync[F[_]](client: SsmClient): Param[F] =
    new Param[F] {
      override final def apply(key: String): ConfigValue[F, String] =
        ConfigValue.blocking {
          val r: ConfigValue[Effect, String] = ConfigValue.suspend[Effect, String] {
            val configKey = buildKey(key)
            try {
              val result = client.getParameter(buildRequest(key))
              awsResponseToConfigValue(configKey, result)
            } catch {
              case _: ParameterNotFoundException => ConfigValue.missing(configKey)
            }
          }
          r
        }
    }

  final def fromAsync[F[_]: Async](client: SsmAsyncClient): Param[F] =
    new Param[F] {
      override final def apply(key: String): ConfigValue[F, String] =
        ConfigValue.blocking {
          ConfigValue.eval {
            val configKey = buildKey(key)
            Async[F]
              .fromCompletableFuture(
                Async[F].delay(
                  client.getParameter(buildRequest(key))
                )
              )
              .map(result => awsResponseToConfigValue(configKey, result).covary[F])
              .handleError {
                case _: ParameterNotFoundException => ConfigValue.missing(configKey)
              }
          }

        }
    }

}
