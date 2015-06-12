package com.dealertrack

import org.apache.commons.io.FileUtils

object Test {

  val defaultPrefix = "/tmp/spark-bug"

  def main(args: Array[String]): Unit = {
    val locationPrefix = args.toList match {
      case Nil => FileUtils.deleteDirectory(new java.io.File(defaultPrefix)); defaultPrefix
      case prefix :: Nil => prefix
    }

    GenerateDataOne.execute(locationPrefix)
    GenerateDataTwo.execute(locationPrefix)
    ErrorExample2.execute(locationPrefix)
    ProcessOutput.execute(locationPrefix)
  }

}
