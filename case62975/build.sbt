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

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.5"

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.2-RC1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.0-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit"), ExclusionRule(organization = "org.apache.avro", name = "avro-mapred")),
  "org.apache.spark" %% "spark-streaming" % "1.3.0-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit"), ExclusionRule(organization = "org.apache.avro", name = "avro-mapred")),
  "org.apache.spark" %% "spark-streaming-kafka" % "1.3.0-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit"), ExclusionRule(organization = "org.apache.avro", name = "avro-mapred"))
)

libraryDependencies += "com.dealer" % "spark-bug-avro-model" % "0.0.1-SNAPSHOT"

// trying to eliminate java security warning
//libraryDependencies += "org.mortbay.jetty" % "servlet-api" % "2.5-20081211"
libraryDependencies += "org.mortbay.jetty" % "servlet-api" % "3.0.20100224"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1"
//libraryDependencies += "javax.servlet" % "servlet-api" % "2.5"

libraryDependencies ++= Seq(
    "org.apache.hadoop" % "hadoop-common" % "2.6.0-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit")),
    "org.apache.hadoop" % "hadoop-client" % "2.6.0-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit")),
    "org.apache.avro" % "avro-mapred" % "1.7.6-cdh5.4.1" classifier "hadoop2" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit")),
    "org.apache.avro" % "avro" % "1.7.6-cdh5.4.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.eclipse.jetty.orbit"))
)

/*
libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
)
*/

