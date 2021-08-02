import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val scala = "2.13.3"
lazy val akka = "2.6.8"

lazy val root = (project in file("."))
  .settings(
    name := "cinema-framework",
    organization := "net.codedone",
    description := "cinema",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    scalaVersion := scala,
    publishMavenStyle := true,
    bintrayRepository := "cinema",
    bintrayOrganization in bintray := None,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("reference.conf") => MergeStrategy.concat
      case PathList("application.conf") => MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    resolvers  ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scala,
      "com.typesafe.akka" %% "akka-actor-typed" % akka,
      "com.typesafe.akka" %% "akka-slf4j" % akka,
      "com.chuusai" %% "shapeless" % "2.4.0-M1",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "5.4.0.201906121030-r",
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akka % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test)
  )
