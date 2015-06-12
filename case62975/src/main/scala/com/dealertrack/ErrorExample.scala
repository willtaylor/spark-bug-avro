package com.dealertrack

import com.dealer.analytics.ad.intake.RTBImpression
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
    val source = "hdfs://vtqaana-cloudera01.dealer.ddc:8020/ad/RTBImpression/incoming/2015/04/27/13/"

    val job = new JobConf()
    job.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")
    FileInputFormat.setInputPaths(job, source)
    val hRdd = sc.hadoopRDD(
      job,
      classOf[org.apache.avro.mapred.AvroInputFormat[RTBImpression]],
      classOf[org.apache.avro.mapred.AvroWrapper[RTBImpression]],
      classOf[org.apache.hadoop.io.NullWritable]
    )

    val totalPrice = hRdd.map(avroMsg => avroMsg._1.datum())
      .map(impression => impression.getDecryptedPrice)
      .filter(x => x != null)
      .reduce((priceX,priceY) => priceX + priceY)

    println(totalPrice)



  }

}
