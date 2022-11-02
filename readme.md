## Ciris AWS SSM

[Amazon EC2 Systems Manager](https://aws.amazon.com/ec2/systems-manager/?nc2=h_m1) (also known as Simple Systems Manager or SSM) support for [Ciris](https://cir.is).

Enables us to load parameters of type "string" or "secure string" from the SSM parameter store.

See [this blog](https://medium.com/@mda590/simple-secrets-management-via-aws-ec2-parameter-store-737477e19450) for more information about using SSM's parameter store to manage secrets.

### Getting Started

To get started with [sbt](https://www.scala-sbt.org), simply add the following lines to your `build.sbt` file.

```scala
resolvers += "Kaluza artifactory" at "https://kaluza.jfrog.io/artifactory/maven"

libraryDependencies += "com.ovoenergy" %% "ciris-aws-ssm" % "LATEST_VERSION"
```

The library is published against cats effect 3, for both Scala 2.12 and 2.13.

### Usage

```scala
import cats.effect.{IO, IOApp}
import cats.implicits._
import ciris.Secret
import ciris.aws.ssm._

object Main extends IOApp.Simple {

  final case class Config(
     username: String,
     password: Secret[String],
     port: Int,
     apiKey: Option[String]
  )

  def loadConfig: IO[Config] = 
    params[IO].flatMap { param =>
      (
        param("/myapp/password").secret,
        param("/myapp/port").as[Int],
        param("/myapp/api-key").option
        ).parMapN { (password, port, apiKey) =>
        Config(username = "Dave", password = password, port = port, apiKey = apiKey)
      }
    }.load

  override def run: IO[Unit] =
    loadConfig.flatMap(cfg => IO.println(s"Config loaded: $cfg"))

}
```
The `params[F]` function returns a ciris `ConfigValue` loader.  On invocation of the (flatMapped) `param(name)`
function, this loader will make a `GetParameterRequest` to the underlying
[SsmAsyncClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ssm/SsmAsyncClient.html),
(decrypting the value if encrypted).


The `params[F]` function uses the AWS SDK's default `SsmAsyncClient` configuration, but there are alternate
functions for construction:

```scala
import ciris.aws.ssm._
import cats.effect.IO
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmAsyncClient

// use the default client, but with a region
params[IO](Region.EU_WEST_1)

// also define a custom creds provider
val customCreds = DefaultCredentialsProvider.builder().profileName("foo").build()
params[IO](Region.EU_WEST_1, customCreds)

// for most flexibility, configure the entire client yourself
val client = SsmAsyncClient.builder().httpClient(customClient).build()
params[IO](client)
```

### Release

To release a new version use the following commands.

```
$ ./tag.sh
$ sbt +publish
```
