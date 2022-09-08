ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .aggregate(sub1, sub2)
  .settings(
    name := "Assignment1"
  )

lazy val sub1 = (project in file("Assignment1.1")).dependsOn(sub2)


lazy val sub2 = (project in file("Assignment1.2"))
