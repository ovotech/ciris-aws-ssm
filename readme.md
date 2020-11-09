## Ciris AWS SSM

[Amazon EC2 Systems Manager](https://aws.amazon.com/ec2/systems-manager/?nc2=h_m1) (also known as Simple Systems Manager or SSM) support for [Ciris](https://cir.is).

Enables us to load parameters of type "string" or "secure string" from the SSM parameter store.

See [this blog](https://medium.com/@mda590/simple-secrets-management-via-aws-ec2-parameter-store-737477e19450) for more information about using SSM's parameter store to manage secrets.

### Getting Started

To get started with [sbt](https://www.scala-sbt.org), simply add the following lines to your `build.sbt` file.

```scala
resolvers += Resolver.bintrayRepo("ovotech", "maven")

libraryDependencies += "com.ovoenergy" %% "ciris-aws-ssm" % "1.0.1"
```

The library is published for Scala 2.12 and 2.13.

### Example

```scala
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.implicits._
import ciris._
import ciris.aws.ssm._
import software.amazon.awssdk.regions.Region

final case class Config(
  username: String,
  password: String,
  port: Int,
  apiKey: Option[String]
)

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      val config =
        for {
          region <- env("AWS_REGION").map(Region.of).default(Region.EU_WEST_1)
          param <- params(blocker, region)
          config <- (
              param("password"),
              param("port").as[Int],
              param("api-key").option
            ).parMapN { (password, port, apiKey) =>
              Config(
                username = "Dave",
                password = password,
                port = port,
                apiKey = apiKey
              )
            }
        } yield config

      config.load[IO].as(ExitCode.Success)
    }
}
```

### Release

To release a new version, use the following command.

```
$ sbt release
```
