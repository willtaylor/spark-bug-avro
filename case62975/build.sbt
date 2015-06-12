import sun.security.tools.PathList

name := "case62975"

version := "1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "ddc-artifactory" at "http://artifactory.dealer.ddc/artifactory/all-repos",
  "snapshots" at "http://artifactory.dealer.ddc/artifactory/all-repos"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.0-cdh5.4.1",
  "org.apache.spark" %% "spark-streaming" % "1.3.0-cdh5.4.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.3.0-cdh5.4.1"
)

// trying to eliminate java security warning
libraryDependencies += "org.mortbay.jetty" % "servlet-api" % "3.0.20100224" % "provided"

dependencyOverrides += "com.google.guava" % "guava" % "14.0.1"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"

libraryDependencies += "org.scalaj" %% "scalaj-http" % "1.1.4"

libraryDependencies += "org.apache.kafka" %% "kafka" % "0.8.2.1"

libraryDependencies ++= Seq(
//  avro, avroMapred
    "org.apache.avro" % "avro-mapred" % "1.7.6-cdh5.3.0" % "provided",
    "org.apache.avro" % "avro" % "1.7.6-cdh5.3.0" % "provided"
)

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
)

libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.2" % "test")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := false
