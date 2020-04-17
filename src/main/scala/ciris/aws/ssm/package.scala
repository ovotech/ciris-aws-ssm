package ciris.aws

import cats.effect.{Blocker, Resource, Sync}
import com.amazonaws.auth._
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement._

package object ssm {
  final def params[F[_]](blocker: Blocker)(
    implicit F: Sync[F],
    region: Regions = sys.env.get("AWS_REGION").map(Regions.fromName).getOrElse(Regions.EU_WEST_1),
    credsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()
  ): Resource[F, Param] =
    Resource {
      F.delay {
        val client =
          AWSSimpleSystemsManagementClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(credsProvider)
            .build()

        val shutdown =
          F.delay(client.shutdown())

        (Param(client, blocker), shutdown)
      }
    }
}
