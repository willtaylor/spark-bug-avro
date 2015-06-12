package com.dealertrack

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.hadoop.mapred.FileInputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * @author ddcjoshuad
 */
object ErrorExample {
  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[2]").setAppName("ImpressionAggregator")
    val sc = new SparkContext(conf)
    //    val source = getClass.getResource("/2015-03-24-18--vtdevana-cloudera14.dealer.ddc-RTBParsedImpression.1427222183676.avro").getPath

    // this hdfs instance may not be the master, if you see an error to that effect,
    // look at the hdfs cluster here: http://vtdevana-cloudera10.dealer.ddc:7180/cmf/services/37/status
    val source = "/tmp/spark-bug"

    val job = new JobConf()
    job.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
    FileInputFormat.setInputPaths(job, source)
    val d1Rdd = sc.hadoopRDD(
      job,
      classOf[org.apache.avro.mapred.AvroInputFormat[DataOne]],
      classOf[org.apache.avro.mapred.AvroWrapper[DataOne]],
      classOf[org.apache.hadoop.io.NullWritable]
    ).map(d1 => d1._1.datum().myId -> d1._1.datum())

    val d2Rdd = sc.hadoopRDD(
      job,
      classOf[org.apache.avro.mapred.AvroInputFormat[DataTwo]],
      classOf[org.apache.avro.mapred.AvroWrapper[DataTwo]],
      classOf[org.apache.hadoop.io.NullWritable]
    ).map(d2 => d2._1.datum().differentId -> d2._1.datum())


    d1Rdd.leftOuterJoin(d2Rdd).foreach{tup  => {
        tup._2._2 match {
          case Some(d2) => print(s"Key: ${tup._1} | d1key: ${tup._2._1.getMyId} | d2key: ${d2.getDifferentId}")
        }
      }
    }

  }

}
