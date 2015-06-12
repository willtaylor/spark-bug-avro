package com.dealertrack

import java.util.UUID

import com.dealer.spark.example.DataOne
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroJob}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.SparkContext._

import scala.util.Random

object GenerateData {

  def main(args: Array[String]) {
    val random = new Random()

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator")
    val sc = new SparkContext(conf)

    try {
      val randomCharStream = new Random().alphanumeric.grouped(25)
      def nextId: String = randomCharStream.next().mkString

      val lotsOfData = Seq.fill(50000) {
        DataOne.newBuilder().setMyId(UUID.randomUUID().toString).setSomeData(random.nextString(10000)).build
      }

      val job = new Job()
      val schema = DataOne.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path("/tmp/spark-bug"))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataOne]])

      sc.parallelize(lotsOfData).map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}
