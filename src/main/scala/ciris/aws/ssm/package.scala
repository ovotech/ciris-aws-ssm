package ciris.aws

import cats.effect.{Blocker, IO, Resource}
import ciris.ConfigValue
import software.amazon.awssdk.auth.credentials.{AwsCredentialsProvider, DefaultCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient

package object ssm {
  def params(
    blocker: Blocker,
    region: Region
  ): ConfigValue[Param] =
    params(blocker, region, DefaultCredentialsProvider.create())

  def params(
    blocker: Blocker,
    region: Region,
    credentials: AwsCredentialsProvider
  ): ConfigValue[Param] =
    ConfigValue.resource {
      Resource.fromAutoCloseable {
        IO {
          val client =
            SsmClient
              .builder()
              .region(region)
              .credentialsProvider(credentials)
              .build()

          ConfigValue.default(Param(client, blocker))
        }
      }
    }
}
