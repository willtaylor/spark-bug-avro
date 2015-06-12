package com.dealertrack

import java.util.UUID

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroJob}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.SparkContext._

import scala.reflect.ClassTag
import scala.util.Random

object GenerateData {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
      val randomCharStream = new Random().alphanumeric.grouped(25)
      def nextId: String = randomCharStream.next().mkString

      val numbers = Seq.fill(50000) { foo: Int => foo }

      val job = new Job()
      val schema = DataOne.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path("/tmp/spark-bug"))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataOne]])

      sc.parallelize(numbers).map { foo =>
        val random = new Random()
        DataOne.newBuilder().setMyId(UUID.randomUUID().toString).setSomeData(random.nextString(10000)).build
      }.map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}
