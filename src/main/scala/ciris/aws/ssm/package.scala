package ciris.aws

import cats.effect.{Blocker, Effect, Resource}
import ciris.ConfigValue
import com.amazonaws.auth._
import com.amazonaws.services.simplesystemsmanagement._

package object ssm {
  def params[F[_]](
    blocker: Blocker,
    region: Region
  )(implicit F: Effect[F]): ConfigValue[Param] =
    params(blocker, region, new DefaultAWSCredentialsProviderChain())

  def params[F[_]](
    blocker: Blocker,
    region: Region,
    credentials: AWSCredentialsProvider
  )(implicit F: Effect[F]): ConfigValue[Param] =
    ConfigValue.resource {
      Resource {
        F.delay {
          val client =
            AWSSimpleSystemsManagementClientBuilder
              .standard()
              .withRegion(region.asJava)
              .withCredentials(credentials)
              .build()

          val shutdown =
            F.delay(client.shutdown())

          (ConfigValue.default(Param(client, blocker)), shutdown)
        }
      }
    }
}
