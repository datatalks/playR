name := """play&R"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  evolutions,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.planet42" %% "laika-core" % "0.5.1",  // Scala from MD to HTML  https://github.com/planet42/Laika https://planet42.github.io/Laika/using-laika/markup.html
  "org.scala-lang" % "scala-compiler" % "2.11.7",  //  For >>> tools is not a member of package scala
  "com.github.tototoshi" % "slick-joda-mapper_2.11" % "2.1.0",
  "be.objectify" %% "deadbolt-scala" % "2.5.0",
  "com.github.stuxuhai" % "jpinyin" % "1.1.7",
  "com.typesafe.netty" % "netty-reactive-streams-http" % "1.0.6",
  "org.jsoup" % "jsoup" % "1.7.2"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
