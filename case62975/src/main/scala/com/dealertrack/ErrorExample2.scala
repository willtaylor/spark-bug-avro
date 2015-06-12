package com.dealertrack

import com.dealer.spark.example.{DataOne, DataTwo}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.AvroKeyInputFormat
import org.apache.hadoop.io.NullWritable
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object ErrorExample2 {
  def main(args: Array[String]): Unit ={

    val conf = new SparkConf().setMaster("local[3]").setAppName("ImpressionAggregator").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    val rddOne: RDD[(String, DataOne)] = sc.union(GenerateDataOne.outputFiles.map { fileName =>
      sc.newAPIHadoopFile(fileName)(ClassTag(classOf[AvroKey[DataOne]]), ClassTag(classOf[NullWritable]), ClassTag(classOf[AvroKeyInputFormat[DataOne]])) map { data: (AvroKey[DataOne], NullWritable) =>
        val (key: AvroKey[DataOne], _) = data
        val obj = key.datum
        obj.getMyId -> obj
      }
    })

    val rddTwo: RDD[(String, DataTwo)] = sc.newAPIHadoopFile(GenerateDataTwo.outputLocation)(ClassTag(classOf[AvroKey[DataTwo]]), ClassTag(classOf[NullWritable]), ClassTag(classOf[AvroKeyInputFormat[DataTwo]])) map { data: (AvroKey[DataTwo], NullWritable) =>
      val (key: AvroKey[DataTwo], _) = data
      val obj = key.datum
      obj.getDifferentId -> obj
    }

    rddOne.leftOuterJoin(rddTwo).foreach{tup: (String, (DataOne, Option[DataTwo]))  => {
      val (key, (d1, d2opt)) = tup
        d2opt match {
          case Some(d2) if ( !d2.getDifferentId.equals(d1.getMyId) || !key.equals(d2.getDifferentId) || !key.equals(d1.getMyId) ) =>
            println(s"Key: ${tup._1} | d1key: ${tup._2._1.getMyId} | d2key: ${d2.getDifferentId}")
          case _ => // ignore good matches
        }
      }
    }

  }

}
