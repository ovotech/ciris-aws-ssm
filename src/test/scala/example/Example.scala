package example

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions

class Example {

  import cats.implicits._
  import ciris._
  import ciris.aws.ssm._

  // the region and credentials provider have default values but can be overriden using implicits
  implicit val region: Regions = Regions.EU_WEST_1
  implicit val credsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain

  case class Config(username: String, password: String, port: Int, alwaysNone: Option[String])

  val config = (
    param("password"),
    param("port").as[Int],
    param("an-absolutely-random-entry").option
  ).parMapN { (password, port, alwaysNone) =>
    Config(username = "Dave", password = password, port = port, alwaysNone = alwaysNone)
  }
}
