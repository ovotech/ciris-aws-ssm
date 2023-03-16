package ciris.aws

import cats.effect.Async
import cats.effect.kernel.{Resource, Sync}
import ciris.ConfigValue
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmAsyncClient

package object ssm {

  /** An asynchronous loader for SSM parameters, using the default client config */
  def params[F[_]: Async]: ConfigValue[F, Param[F]] = params(
    SsmAsyncClient.builder().build()
  )

  /** An asynchronous loader for SSM parameters, using the default client config with some overrides
    *
    * @param region
    *   The AWS Region
    * @param credsProvider
    *   optional credentials provider to use (default is `DefaultCredentialsProvider`)
    */
  def params[F[_]: Async](
    region: Region,
    credsProvider: DefaultCredentialsProvider = DefaultCredentialsProvider.builder().build()
  ): ConfigValue[F, Param[F]] = params(
    SsmAsyncClient.builder().region(region).credentialsProvider(credsProvider).build()
  )

  /** An asynchronous loader for SSM parameters, using the provided `SsmClient` */
  def params[F[_]: Async](client: SsmAsyncClient): ConfigValue[F, Param[F]] =
    ConfigValue.resource {
      Resource
        .fromAutoCloseable[F, SsmAsyncClient](Sync[F].delay(client))
        .map(client => ConfigValue.default(Param.fromAsync[F](client)))
    }

}
