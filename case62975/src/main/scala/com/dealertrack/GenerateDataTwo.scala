package com.dealertrack

import java.util.UUID

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyInputFormat, AvroJob, AvroKeyOutputFormat}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag
import scala.util.Random

object GenerateDataTwo {

  def outputLocation(prefix: String) = prefix + "/data-two"

  private val matchThreshold = 10
  private val dataSize = 2500

  def main(args: Array[String]) {
    execute(Test.defaultPrefix)
  }

  def execute(prefix: String) = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator 2").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
      val rdd = sc.newAPIHadoopFile(GenerateDataOne.outputLocation(prefix))(ClassTag(classOf[AvroKey[DataOne]]), ClassTag(classOf[NullWritable]), ClassTag(classOf[AvroKeyInputFormat[DataOne]])) map {
        ((data: AvroKey[DataOne], _: NullWritable) => data datum).tupled
      }

      val data2 = rdd map { dataOne =>
        val random = new Random()
        if (random.nextInt(100) <= matchThreshold) {
          DataTwo.newBuilder().setDifferentId(dataOne.getMyId).setSomeOtherData(random.nextString(dataSize))
        } else {
          DataTwo.newBuilder().setDifferentId(UUID.randomUUID().toString).setSomeOtherData(random.nextString(dataSize))
        }
      }

      val job = new Job()
      val schema = DataTwo.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path(outputLocation(prefix)))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataTwo]])

      data2.map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}
