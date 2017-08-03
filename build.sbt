name := "link checker"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++=
  ("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/") ::
  ("spray repo" at "http://repo.spray.io") ::
  Nil

libraryDependencies ++=
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1" ::
  "com.typesafe.akka" %% "akka-actor" % "2.3.0" ::
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0" % "test" ::
  "io.spray" % "spray-can" % "1.3.1" ::
  Nil
