package co.com.psl.training.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{functions => sqlfun}

/** Simple Spark with Scala introductory workshop. */
object Introduction {
  /** Path to the apartments CSV file. */
  lazy val ApartmentsPath: String =
    java.nio.file.Paths.get(
      this.getClass.getResource("/apartments.csv").toURI()
    ).toAbsolutePath().normalize().toString()

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
    import spark.implicits._

    // ------------------------------------------ RDDs --------------------------------------------
    println("RDDs -----------------------------------------")

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

    // Display our results.
    println(s"WordCount results: ${wordCount}")

    println("----------------------------------------------")
    println()
    // --------------------------------------------------------------------------------------------

    // ------------------------------------------ DataFrames --------------------------------------
    // Read a DataFrame from a CSV file.
    println("DataFrames -----------------------------------")
    val apartments = // apartments: DataFrame = [name: string, area: double, price double]
      spark
        .read
        .option("header", "true")
        .option("encoder", "UTF-8")
        .option("sep", ",")
        .option("inferSchema", "true")
        .csv(ApartmentsPath)

    // Let's give a look to the data and the inferred schema
    println("Apartments schema:")
    apartments.printSchema()
    println("Apartments first five rows:")
    apartments.show(numRows  = 5, truncate = false)

    // Compute some basic statistics, like: min, mean & max values, on the data.
    println("Apartments basic statistics:")
    apartments.select($"area", $"price").summary().show(truncate = false)

    // Let's find the apartments whose price per square meter is bellow the mean.
    val apartmentsPSM = // PSM = price per square meter
      apartments.select(
        $"name", $"area",
        $"price",
        ($"price" / $"area").as("psm")
      )
    val meanPSM =
      apartmentsPSM.select(
        sqlfun.mean($"psm").as("mean-psm")
      ).first().getAs[Double](fieldName = "mean-psm")
    val aparmentsPSMBelowMean = apartmentsPSM.where($"psm" < sqlfun.lit(meanPSM))

    // Display our results.
    println(s"Mean price per square meter: ${meanPSM}")
    println("Apartments whose price per square meter is bellow the mean")
    aparmentsPSMBelowMean.show(truncate = false)

    println("----------------------------------------------")
    println()
    // --------------------------------------------------------------------------------------------

    // Stop the Spark Session and exit.
    spark.stop()
  }
}
