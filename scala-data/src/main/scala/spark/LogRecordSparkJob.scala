package spark

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._


object LogRecordSparkJob {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("CassandraSparkJob")
      .master("local[*]")
      .config("spark.cassandra.connection.host", "localhost:9042")
      .getOrCreate()

    val data = spark.read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "log_record", "keyspace" -> "ned_learning"))
      .load()


//    val updatedData = data.withColumn("operate_day",
//      trunc(date_format(col("operate_date"), "yyyy-MM-dd"), "day"))


    // Convert timestamp to date format
    val dataWithDate = data.withColumn("operate_day",
      date_format(col("operate_date"), "yyyy-MM-dd"))

    // Group by biz_id and the new operate_day column
    val bizIdAggregation = dataWithDate.groupBy("biz_id", "operate_day")
      .agg(count("biz_id").as("count_per_biz_id"))

    bizIdAggregation.show()

    // Group by client_ip and the new operate_day column
    val clientIpAggregation = dataWithDate.groupBy("client_ip", "operate_day")
      .agg(count("client_ip").as("count_per_client_ip"))

    clientIpAggregation.show()


    // 将biz_id的聚合数据保存到Cassandra
    bizIdAggregation.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "biz_id_aggregation", "keyspace" -> "ned_learning"))
      .mode(SaveMode.Append)
      .save()

    // 将client_ip的聚合数据保存到Cassandra
    clientIpAggregation.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "client_ip_aggregation", "keyspace" -> "ned_learning"))
      .mode(SaveMode.Append)
      .save()


  }
}
