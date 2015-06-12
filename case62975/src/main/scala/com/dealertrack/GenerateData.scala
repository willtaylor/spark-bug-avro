package com.dealertrack

import com.dealer.spark.example.DataOne
import org.apache.avro.Schema
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroJob}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.{FileInputFormat, JobConf}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkContext, SparkConf}

import scala.util.Random

object GenerateData {

  def main(args: Array[String]) {
    val random = new Random()

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator")
    val sc = new SparkContext(conf)

    val conf = new Job()
    val schema = DataOne.SCHEMA$

    FileOutputFormat.setOutputPath(conf, new Path("/tmp/spark-bug"))
    AvroJob.setOutputKeySchema(conf, schema)
    conf.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataOne]])

    output.good.map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(conf.getConfiguration)

  }

}
