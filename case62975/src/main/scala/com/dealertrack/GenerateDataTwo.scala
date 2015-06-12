package com.dealertrack

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

  val outputLocation = "/tmp/spark-bug/data-two"

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[2]").setAppName("Sample Data Generator 2").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
      val rdd = sc.newAPIHadoopFile(GenerateDataOne.outputLocation)(ClassTag(classOf[AvroKey[DataOne]]), ClassTag(classOf[NullWritable]), ClassTag(classOf[AvroKeyInputFormat[DataOne]])) map {
        ((data: AvroKey[DataOne], _: NullWritable) => data datum).tupled
      }

      val data2 = rdd.filter { item =>
        val random = new Random()
        random.nextInt(100) <= 10
      } map { dataOne =>
        val random = new Random()
        DataTwo.newBuilder().setDifferentId(dataOne.getMyId).setSomeOtherData(random.nextString(1000))
      }

      val job = new Job()
      val schema = DataTwo.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path(outputLocation))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataTwo]])

      data2.map(new AvroKey(_) -> NullWritable.get).saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}