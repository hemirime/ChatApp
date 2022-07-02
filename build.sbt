ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ChatApp",
    idePackagePrefix := Some("com.github.hemirime.chatapp")
  )

scalacOptions += "-Wconf:cat=other-match-analysis:error"

libraryDependencies ++= {
  val AkkaVersion = "2.6.19"
  val AkkaHttpVersion = "10.2.9"
  val SlickVersion = "3.3.3"

  Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  ) ++ Seq(
    "com.typesafe.play" %% "play-json" % "2.9.2",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.39.2",
  ) ++ Seq(
    "com.typesafe.slick" %% "slick" % SlickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
    "org.postgresql" % "postgresql" % "42.3.6",
  ) :+ "ch.qos.logback" % "logback-classic" % "1.2.11"
}
