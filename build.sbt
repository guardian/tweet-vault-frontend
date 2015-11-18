name := """tweet-vault-frontend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.4"
libraryDependencies += "ch.qos.logback"      %   "logback-classic" % "1.1.2"
libraryDependencies += "org.json4s"          %% "json4s-native"    % "3.2.10"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "1.7.3"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

