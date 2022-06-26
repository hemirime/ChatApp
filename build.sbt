ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ChatApp",
    idePackagePrefix := Some("com.github.hemirime.chatapp")
  )
