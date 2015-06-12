package com.dealertrack

import com.dealer.spark.example.{DataOne, DataTwo, DataJoin}
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroJob, AvroKeyInputFormat}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object ErrorExample2 {

  val outputLocation = "/tmp/spark-bug/data-join"

  def main(args: Array[String]) {
    execute
  }

  def execute() = {

    val conf = new SparkConf().setMaster("local[3]").setAppName("Reproduce Bug").registerKryoClasses(Array(classOf[DataOne], classOf[DataTwo]))
    val sc = new SparkContext(conf)

    try {
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

      val job = new Job()
      val schema = DataJoin.SCHEMA$

      FileOutputFormat.setOutputPath(job, new Path(outputLocation))
      AvroJob.setOutputKeySchema(job, schema)
      job.setOutputFormatClass(classOf[AvroKeyOutputFormat[DataJoin]])

      rddOne.leftOuterJoin(rddTwo).map { tup: (String, (DataOne, Option[DataTwo])) => {
        val (key, (d1, d2opt)) = tup
        d2opt match {
          case Some(d2) => DataJoin.newBuilder().setKey(key)
            .setDataOneKey(d1.getMyId)
            .setDataOne(d1.getSomeData)
            .setDataTwoKey(d2.getDifferentId)
            .setDataTwo(d2.getSomeOtherData)
            .build()
          case None => DataJoin.newBuilder().setKey(key)
            .setDataOneKey(d1.getMyId)
            .setDataOne(d1.getSomeData)
            .build()
        }
      }
      }.map {
        new AvroKey(_) -> NullWritable.get
      }.saveAsNewAPIHadoopDataset(job.getConfiguration)
    } finally {
      sc.stop()
    }

  }

}
