name := "sleveldbjni"
     
version := "0.1-SNAPSHOT"
     
scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  // test
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  //log
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
)
