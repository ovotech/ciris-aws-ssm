package ciris.aws

import cats.effect.kernel.{Resource, Sync}
import ciris.ConfigValue
import software.amazon.awssdk.auth.credentials.{AwsCredentialsProvider, DefaultCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient

package object ssm {
  def params[F[_]: Sync](region: Region
  ): ConfigValue[F, Param[F]] =
    params(region, DefaultCredentialsProvider.create())

  def params[F[_]: Sync](
    region: Region,
    credentials: AwsCredentialsProvider
  ): ConfigValue[F, Param[F]] =
    ConfigValue.resource {
      Resource
        .fromAutoCloseable[F, SsmClient] {
          Sync[F].delay {
            SsmClient
              .builder()
              .region(region)
              .credentialsProvider(credentials)
              .build()

          }
        }
        .map(client => ConfigValue.default(Param[F](client)))
    }
}
