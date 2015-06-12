package com.dealertrack

import org.apache.commons.io.FileUtils

object Test {

  val defaultPrefix = "/tmp/spark-bug"

  def main(args: Array[String]): Unit = {
    val locationPrefix = args.toList match {
      case Nil => FileUtils.deleteDirectory(new java.io.File(defaultPrefix)); defaultPrefix
      case prefix :: Nil => prefix
    }

    println(s"Using location prefix:${locationPrefix}")

    println("about to generate first batch of test data")
    GenerateDataOne.execute(locationPrefix)

    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("about to generate second batch of test data")
    GenerateDataTwo.execute(locationPrefix)

    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("about to reproduce the error")
    ErrorExample2.execute(locationPrefix)

    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("*************************************************")
    println("about to process the output and display errors")
    ProcessOutput.execute(locationPrefix)
  }

}
