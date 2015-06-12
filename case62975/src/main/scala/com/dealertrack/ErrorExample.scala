package com.dealertrack

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.AvroKeyInputFormat
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.{FileInputFormat, JobConf}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * @author ddcjoshuad
 */
object ErrorExample {
  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[3]").setAppName("ImpressionAggregator").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)
    //    val source = getClass.getResource("/2015-03-24-18--vtdevana-cloudera14.dealer.ddc-RTBParsedImpression.1427222183676.avro").getPath

    // this hdfs instance may not be the master, if you see an error to that effect,
    // look at the hdfs cluster here: http://vtdevana-cloudera10.dealer.ddc:7180/cmf/services/37/status
    val source2 = "/tmp/spark-bug/data-two"
    val source1 = "/tmp/spark-bug/data-one"

    val job1 = new JobConf()
    job1.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
    FileInputFormat.setInputPaths(job1, source1)

    val job2 = new JobConf()
    job2.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
    FileInputFormat.setInputPaths(job2, source2)


    var d1Rdd: RDD[DataOne] = sc.newAPIHadoopFile(source1)(
      ClassTag(classOf[AvroKey[DataOne]]),
      ClassTag(classOf[NullWritable]),
      ClassTag(classOf[AvroKeyInputFormat[DataOne]])
    ).map {
      ((impression: AvroKey[DataOne], _: NullWritable) => impression datum).tupled
    }


    (0 until 10).foreach(_ => d1Rdd = d1Rdd.union(sc.newAPIHadoopFile(source1)(
      ClassTag(classOf[AvroKey[DataOne]]),
      ClassTag(classOf[NullWritable]),
      ClassTag(classOf[AvroKeyInputFormat[DataOne]])
    ).map {
      ((impression: AvroKey[DataOne], _: NullWritable) => impression datum).tupled
    }))


    var d2Rdd: RDD[DataTwo] = sc.newAPIHadoopFile(source2)(
      ClassTag(classOf[AvroKey[DataTwo]]),
      ClassTag(classOf[NullWritable]),
      ClassTag(classOf[AvroKeyInputFormat[DataTwo]])
    ).map {
      ((impression: AvroKey[DataTwo], _: NullWritable) => impression datum).tupled
    }


    (0 until 2).foreach(_ => d2Rdd = d2Rdd.union(sc.newAPIHadoopFile(source2)(
      ClassTag(classOf[AvroKey[DataTwo]]),
      ClassTag(classOf[NullWritable]),
      ClassTag(classOf[AvroKeyInputFormat[DataTwo]])
    ).map {
      ((impression: AvroKey[DataTwo], _: NullWritable) => impression datum).tupled
    }))



    d1Rdd.map(d1 => d1.getMyId -> d1).leftOuterJoin(d2Rdd.map(d2 => d2.getDifferentId -> d2)).filter{ tup =>
      tup._2._2 match {
        case Some(d2) => true
        case _ =>  false
      }
    }.collect().foreach{tup  => {
        tup._2._2 match {
          case Some(d2) => if(!d2.getDifferentId.equals(tup._2._1.getMyId)){
            println(s"Key: ${tup._1} | d1key: ${tup._2._1.getMyId} | d2key: ${d2.getDifferentId}")
          }
          case _ =>
        }
      }
    }

  }

}
