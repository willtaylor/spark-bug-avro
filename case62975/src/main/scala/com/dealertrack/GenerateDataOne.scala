package com.dealertrack

import java.util.UUID

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroJob, AvroKeyOutputFormat}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Random

object GenerateDataOne {

  val outputLocation = "/tmp/spark-bug/data-one"

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator 1").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
      val numbers = Seq.fill(50000) { foo: Int => foo }

      val job = new Job()
      val schema = DataOne.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path(outputLocation))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataOne]])

      sc.parallelize(numbers).map { foo =>
        val random = new Random()
        DataOne.newBuilder().setMyId(UUID.randomUUID().toString).setSomeData(random.nextString(1000)).build
      }.map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}
