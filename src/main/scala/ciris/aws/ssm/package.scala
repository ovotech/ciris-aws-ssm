package ciris.aws

import cats.effect.{Blocker, Effect, IO, Resource}
import ciris.ConfigValue
import com.amazonaws.auth._
import com.amazonaws.services.simplesystemsmanagement._

package object ssm {
  def params(
    blocker: Blocker,
    region: Region
  ): ConfigValue[Param] =
    params(blocker, region, new DefaultAWSCredentialsProviderChain())

  def params(
    blocker: Blocker,
    region: Region,
    credentials: AWSCredentialsProvider
  ): ConfigValue[Param] =
    ConfigValue.resource {
      Resource {
        IO {
          val client =
            AWSSimpleSystemsManagementClientBuilder
              .standard()
              .withRegion(region.asJava)
              .withCredentials(credentials)
              .build()

          val shutdown =
            IO(client.shutdown())

          (ConfigValue.default(Param(client, blocker)), shutdown)
        }
      }
    }
}
