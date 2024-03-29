lazy val root = (project in file("."))
  .settings(publishOptions)
  .settings(
    name := "ciris-aws-ssm",
    organization := "com.ovoenergy",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    scalaVersion := "3.2.2",
    crossScalaVersions := Seq(scalaVersion.value, "2.13.11", "2.12.10"),
    scalacOptions := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => Seq("-Ypartial-unification")
      case _             => Seq.empty
    }),
    libraryDependencies ++= Seq(
      "is.cir" %% "ciris" % "3.2.0",
      "software.amazon.awssdk" % "ssm" % "2.20.93",
      "org.typelevel" %% "cats-effect" % "3.5.1",
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % "test"
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
  ),
  credentials += {
    for {
      usr <- sys.env.get("ARTIFACTORY_USER")
      password <- sys.env.get("ARTIFACTORY_PASS")
    } yield Credentials("Artifactory Realm", "kaluza.jfrog.io", usr, password)
  }.getOrElse(Credentials(Path.userHome / ".sbt" / ".credentials"))
)
