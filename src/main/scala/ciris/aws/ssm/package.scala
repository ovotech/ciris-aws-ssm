package ciris.aws

import cats.effect.{Async, Resource, Sync}
import ciris.ConfigValue
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.{SsmAsyncClient, SsmAsyncClientBuilder}

package object ssm {

  /** An asynchronous loader for SSM parameters, using the default client config */
  def params[F[_]: Async]: ConfigValue[F, Param[F]] = params(SsmAsyncClient.builder())

  /** An asynchronous loader for SSM parameters, otherwise using the default client config with the
    * given region
    *
    * @param region
    *   The AWS Region
    */
  def params[F[_]: Async](region: Region): ConfigValue[F, Param[F]] =
    params(SsmAsyncClient.builder().region(region))

  /** An asynchronous loader for SSM parameters, using the default client config with the given
    * region and credentials provider
    *
    * @param region
    *   The AWS Region
    * @param credsProvider
    *   AWS credentials provider to use
    */
  def params[F[_]: Async](
    region: Region,
    credsProvider: AwsCredentialsProvider
  ): ConfigValue[F, Param[F]] =
    params(SsmAsyncClient.builder().region(region).credentialsProvider(credsProvider))

  private def params[F[_]: Async](builder: SsmAsyncClientBuilder): ConfigValue[F, Param[F]] =
    ConfigValue.resource(Resource.fromAutoCloseable(Sync[F].delay(builder.build())).map(params[F]))

  /** An asynchronous loader for SSM parameters, using the provided `SsmAsyncClient`
    *
    * @param client
    *   The SSM client to use. The caller remains responsible for managing its lifecycle.
    */
  def params[F[_]: Async](client: SsmAsyncClient): ConfigValue[F, Param[F]] =
    ConfigValue.default(Param.fromAsync[F](client))

}
