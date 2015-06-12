name := "case62975"

version := "1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos",
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)

// This is here to include provided scope dependencies when running the app.  See: https://github.com/sbt/sbt-assembly#-provided-configuration
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.0-cdh5.4.1"
)

libraryDependencies += "com.dealer" % "spark-bug-avro-model" % "0.0.1-SNAPSHOT"

// trying to eliminate java security warning
libraryDependencies += "org.mortbay.jetty" % "servlet-api" % "3.0.20100224" % "provided"

libraryDependencies ++= Seq(
    "org.apache.avro" % "avro-mapred" % "1.7.6-cdh5.3.0" % "provided",
    "org.apache.avro" % "avro" % "1.7.6-cdh5.3.0" % "provided"
)

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
)

