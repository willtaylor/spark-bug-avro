package com.dealertrack

import com.dealer.spark.example.{DataJoin, DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroJob, AvroKeyInputFormat, AvroKeyOutputFormat}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object ProcessOutput {

  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[3]").setAppName("Process Output").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
      val rdd: RDD[DataJoin] = sc.newAPIHadoopFile(ErrorExample2.outputLocation)(ClassTag(classOf[AvroKey[DataJoin]]), ClassTag(classOf[NullWritable]), ClassTag(classOf[AvroKeyInputFormat[DataJoin]])) map { data: (AvroKey[DataJoin], NullWritable) =>
        val (key: AvroKey[DataJoin], _) = data
        key.datum
      }

      rdd foreach { entry =>
        val key = entry.getKey
        val data2Key = entry.getDataTwoKey
        val bad = !key.equals(entry.getDataOneKey) || (data2Key != null && !key.equals(data2Key))

        if (bad) {
          println(s"***********************************")
          println(s"***********************************")
          println(s"Bad Record: key:${key}, data1Key:${entry.getDataOneKey}, data2Key:${data2Key}")
          println(s"***********************************")
          println(s"***********************************")
        }
      }
    } finally {
      sc.stop()
    }

  }

}
