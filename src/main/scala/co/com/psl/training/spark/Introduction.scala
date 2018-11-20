package co.com.psl.training.spark

import org.apache.spark.sql.SparkSession

/** Simple Spark with Scala introductory workshop. */
object Introduction {
  /** Application Enty Point */
  def main(args: Array[String]) {
    // Initialize the Spark Session.
    val spark = SparkSession.builder.master("local[*]").appName("Spark Scala Workshop").getOrCreate()
    import spark.implicits._

    // Stop the Spark Session and exit.
    spark.stop()
  }
}
