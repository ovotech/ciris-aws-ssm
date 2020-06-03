package ciris.aws.ssm

import cats.implicits._
import ciris.ConfigDecoder
import com.amazonaws.regions.Regions

sealed abstract class Region(val asJava: Regions) {
  final def name: String =
    asJava.getName()

  final def description: String =
    asJava.getDescription()
}

object Region {
  case object AP_EAST_1 extends Region(Regions.AP_EAST_1)
  case object AP_NORTHEAST_1 extends Region(Regions.AP_NORTHEAST_1)
  case object AP_NORTHEAST_2 extends Region(Regions.AP_NORTHEAST_2)
  case object AP_SOUTH_1 extends Region(Regions.AP_SOUTH_1)
  case object AP_SOUTHEAST_1 extends Region(Regions.AP_SOUTHEAST_1)
  case object AP_SOUTHEAST_2 extends Region(Regions.AP_SOUTHEAST_2)
  case object CA_CENTRAL_1 extends Region(Regions.CA_CENTRAL_1)
  case object CN_NORTH_1 extends Region(Regions.CN_NORTH_1)
  case object CN_NORTHWEST_1 extends Region(Regions.CN_NORTHWEST_1)
  case object EU_CENTRAL_1 extends Region(Regions.EU_CENTRAL_1)
  case object EU_NORTH_1 extends Region(Regions.EU_NORTH_1)
  case object EU_WEST_1 extends Region(Regions.EU_WEST_1)
  case object EU_WEST_2 extends Region(Regions.EU_WEST_2)
  case object EU_WEST_3 extends Region(Regions.EU_WEST_3)
  case object GovCloud extends Region(Regions.GovCloud)
  case object ME_SOUTH_1 extends Region(Regions.ME_SOUTH_1)
  case object SA_EAST_1 extends Region(Regions.SA_EAST_1)
  case object US_EAST_1 extends Region(Regions.US_EAST_1)
  case object US_EAST_2 extends Region(Regions.US_EAST_2)
  case object US_GOV_EAST_1 extends Region(Regions.US_GOV_EAST_1)
  case object US_WEST_1 extends Region(Regions.US_WEST_1)
  case object US_WEST_2 extends Region(Regions.US_WEST_2)

  def apply(region: Regions): Region =
    region match {
      case Regions.AP_EAST_1      => Region.AP_EAST_1
      case Regions.AP_NORTHEAST_1 => Region.AP_NORTHEAST_1
      case Regions.AP_NORTHEAST_2 => Region.AP_NORTHEAST_2
      case Regions.AP_SOUTH_1     => Region.AP_SOUTH_1
      case Regions.AP_SOUTHEAST_1 => Region.AP_SOUTHEAST_1
      case Regions.AP_SOUTHEAST_2 => Region.AP_SOUTHEAST_2
      case Regions.CA_CENTRAL_1   => Region.CA_CENTRAL_1
      case Regions.CN_NORTH_1     => Region.CN_NORTH_1
      case Regions.CN_NORTHWEST_1 => Region.CN_NORTHWEST_1
      case Regions.EU_CENTRAL_1   => Region.EU_CENTRAL_1
      case Regions.EU_NORTH_1     => Region.EU_NORTH_1
      case Regions.EU_WEST_1      => Region.EU_WEST_1
      case Regions.EU_WEST_2      => Region.EU_WEST_2
      case Regions.EU_WEST_3      => Region.EU_WEST_3
      case Regions.GovCloud       => Region.GovCloud
      case Regions.ME_SOUTH_1     => Region.ME_SOUTH_1
      case Regions.SA_EAST_1      => Region.SA_EAST_1
      case Regions.US_EAST_1      => Region.US_EAST_1
      case Regions.US_EAST_2      => Region.US_EAST_2
      case Regions.US_GOV_EAST_1  => Region.US_GOV_EAST_1
      case Regions.US_WEST_1      => Region.US_WEST_1
      case Regions.US_WEST_2      => Region.US_WEST_2
    }

  def unapply(region: Region): Some[Regions] =
    Some(region.asJava)

  def fromName(name: String): Option[Region] =
    Regions.values.find(_.name == name).map(apply)

  implicit val regionConfigDecoder: ConfigDecoder[String, Region] =
    ConfigDecoder[String].mapOption("Region")(fromName)
}
