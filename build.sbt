organization := "com.ovoenergy"
bintrayOrganization := Some("ovotech")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.12.5"
crossScalaVersions := Seq("2.11.12", scalaVersion.value)
releaseCrossBuild := true

scalacOptions += "-language:higherKinds"

libraryDependencies ++= Seq(
  "is.cir" %% "ciris-core" % "0.9.0",
  "com.amazonaws" % "aws-java-sdk-ssm" % "1.11.298"
)
