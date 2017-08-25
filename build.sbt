organization := "com.ovoenergy"
bintrayOrganization := Some("ovotech")

scalaVersion := "2.12.3"
crossScalaVersions := Seq("2.11.11", scalaVersion.value)
releaseCrossBuild := true

libraryDependencies ++= Seq(
  "is.cir" %% "ciris-core" % "0.4.0",
  "com.amazonaws" % "aws-java-sdk-ssm" % "1.11.182"
)
