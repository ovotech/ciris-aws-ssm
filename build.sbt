lazy val root = (project in file("."))
  .settings(publishOptions)
  .settings(
    name := "ciris-aws-ssm",
    organization := "com.ovoenergy",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    scalaVersion := "2.13.1",
    crossScalaVersions := Seq(scalaVersion.value, "2.12.10"),
    libraryDependencies ++= Seq(
      "is.cir" %% "ciris" % "1.2.1",
      "software.amazon.awssdk" % "ssm" % "2.16.62"
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/ovotech/ciris-aws-ssm"),
        "scm:git:git@github.com:ovotech/ciris-aws-ssm.git"
      )
    )
  )

lazy val publishOptions = Seq(
  publishTo := Some(
    "Kaluza artifactory maven public" at "https://kaluza.jfrog.io/artifactory/maven"
  )
)
