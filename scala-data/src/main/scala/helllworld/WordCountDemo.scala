package helllworld

import org.apache.spark.sql.SparkSession

object WordCountDemo {

  def main(args: Array[String]): Unit = {

    // 1. 创建一个 SparkSession
    val spark = SparkSession.builder()
      .appName("Word Count Demo")
      .master("local[*]") // 使用所有可用的核，本地运行。在实际环境中可能需要设置为集群的 master URL。
      .getOrCreate()

    // 2. 读取文本文件
    val textFile = spark.sparkContext.textFile(
      "/Users/nedonion/Documents/iCollections/Folders/Backend-Learning/springboot-cassandra-crud/scala-data/src/main/resources/sample.txt")

    // 3. 进行单词计数
    val wordCounts = textFile
      .flatMap(line => line.split("\\W+")) // 使用非单词字符拆分行
      .filter(word => word.nonEmpty) // 过滤掉空单词
      .map(word => (word.toLowerCase, 1)) // 转为小写并映射为 (word, 1)
      .reduceByKey(_ + _) // 按单词分组并计算总数

    // 4. 输出结果
    wordCounts.collect().foreach {
      case (word, count) => println(s"$word: $count")
    }

    // 5. 关闭 SparkSession
    spark.stop()
  }
}

