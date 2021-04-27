organization := "com.ovoenergy"
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.13.1"
crossScalaVersions := Seq(scalaVersion.value, "2.12.10")

libraryDependencies ++= Seq(
  "is.cir" %% "ciris" % "1.2.1",
  "software.amazon.awssdk" % "ssm" % "2.16.48"
)

publishTo := Some("Kaluza artifactory maven public" at "https://kaluza.jfrog.io/artifactory/maven")
