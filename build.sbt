ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "io.github.kirill5k"
ThisBuild / organizationName := "example"
ThisBuild / resolvers ++= Seq(
  "Confluent" at "https://packages.confluent.io/maven/",
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("public"),
  Resolver.sbtPluginRepo("releases")
)

releaseVersionBump := sbtrelease.Version.Bump.Next
releaseCrossBuild := false

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publish / skip := true
)

lazy val root = (project in file("."))
  .settings(noPublish)
  .settings(
    name := "kafka-connect-http"
  )
  .aggregate(sink)

lazy val commonSettings = Seq(
  organizationName := "Kafka Connect Http",
  startYear := Some(2020),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  resolvers += "Apache public" at "https://repository.apache.org/content/groups/public/",
  scalafmtOnCompile := true
)

lazy val sink = (project in file("connectors/sink"))
  .settings(commonSettings)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "kafka-connect-http-sink",
    moduleName := "kafka-connect-http-sink",
    libraryDependencies ++= Dependencies.sink ++ Dependencies.test,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    assembly / assemblyJarName := "kafka-connect-http-sink.jar",
    assembly / assemblyOption := (assembly / assemblyOption).value.copy(
      includeScala = false,
      includeDependency = true
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    },
    artifact in (Compile, assembly) := {
      val art = (artifact in (Compile, assembly)).value
      art.withClassifier(Some("assembly"))
    },
    addArtifact(artifact in (Compile, assembly), assembly)
  )
  .enablePlugins(AutomateHeaderPlugin)
