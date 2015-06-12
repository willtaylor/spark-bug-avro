package com.dealertrack

import org.apache.commons.io.FileUtils

object Test {

  def main(args: Array[String]): Unit = {
    FileUtils.deleteDirectory(new java.io.File("/tmp/spark-bug"))

    GenerateDataOne.execute()
    GenerateDataTwo.execute()
    ErrorExample2.execute()
    ProcessOutput.execute()
  }

}
