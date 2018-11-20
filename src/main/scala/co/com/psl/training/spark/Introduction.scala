package co.com.psl.training.spark

import org.apache.spark.sql.SparkSession

/** Simple Spark with Scala introductory workshop. */
object Introduction {
  /** Application Enty Point */
  def main(args: Array[String]) {
    // Initialize the Spark Session.
    val spark =
      SparkSession
        .builder
        .master("local[*]")
        .appName("Spark Scala Workshop - Introduction")
        .getOrCreate()
    val sc = spark.sparkContext
    import spark.implicits._

    // Create an RDD from a Scala collection.
    val data = List(
      "Hello, World!",
      "Hello, Big Data",
      "Goodbye, Spark",
      "Goodbye, World"
    )
    val lines = sc.parallelize(data) // lines: RDD[String]

    // Computes the 'word count' of our text.
    val wordsToRemove = Set("big", "goodbye", "spark")
    val wordCount =
      lines
        .flatMap(line => line.split("\\P{L}")) // Splits by every character that isn't a letter.
        .map(word => word.toLowerCase)
        .filter(word => word.nonEmpty && !wordsToRemove.contains(word))
        .map(word => word -> 1)
        .reduceByKeyLocally((acc, v) => acc + v)

    // Display our results
    println(s"Word Count results: ${wordCount}")

    // Stop the Spark Session and exit.
    spark.stop()
  }
}
