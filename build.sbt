organization := "com.ovoenergy"
bintrayOrganization := Some("ovotech")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.11", scalaVersion.value)
releaseCrossBuild := true

libraryDependencies ++= Seq(
  "is.cir" %% "ciris-core" % "0.5.0",
  "com.amazonaws" % "aws-java-sdk-ssm" % "1.11.275"
)
