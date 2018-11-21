package co.com.psl.training.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/** Exercise to apply what has been learned. */
object Exercise {

  /** Application Entry Point. */
  def main(args: Array[String]) {
    // Initialize the Spark Session.
    val spark =
      SparkSession
        .builder
        .master("local[*]")
        .appName("Spark Scala Workshop - Introduction")
        .getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")
    import spark.implicits._

    // Input data - [user, datetime, location].
    val data = List(
      ("lmejias", "2018-12-31T13:01:00", "home"),
      ("dossass", "2018-12-31T13:05:00", "home"),
      ("lmejias", "2018-12-31T13:10:00", "work"),
      ("dossass", "2018-12-31T13:15:00", "home"),
      ("lmejias", "2018-12-31T13:15:00", "work"),
      ("dossass", "2018-12-31T13:55:00", "cc"),
      ("lmejias", "2018-12-31T13:40:00", "work"),
      ("lmejias", "2018-12-31T15:15:00", "cc"),
      ("lmejias", "2018-12-31T15:30:00", "cc"),
      ("lmejias", "2018-12-31T15:45:00", "cc"),
      ("dossass", "2018-12-31T17:20:00", "cc"),
      ("dossass", "2018-12-31T17:30:00", "cc"),
      ("dossass", "2018-12-31T17:20:00", "home")
    )

    // This function should return all unique locations on which an user was for each hour.
    def exercise(inputData: List[(String, String, String)]): RDD[(String, String, Array[String])] = {
      ???
    }

    // Execute your implementation.
    val results = exercise(data)

    // Show your results.
    results.toDF.show(truncate = false)

    // Stop the Spark Session and exit.
    spark.stop()
  }
}
