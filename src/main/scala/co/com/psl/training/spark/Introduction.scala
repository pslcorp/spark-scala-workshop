package co.com.psl.training.spark

import org.apache.spark.sql.{
  SparkSession,
  Encoder,
  Encoders,
  functions => sqlfun
}
import org.apache.spark.sql.expressions.Aggregator

/** Simple Spark with Scala introductory workshop. */
object Introduction {
  /** Path to the apartments CSV file. */
  lazy val ApartmentsPath: String =
    java.nio.file.Paths.get(
      this.getClass.getResource("/apartments.csv").toURI()
    ).toAbsolutePath().normalize().toString()

  /** Path to the NBA teams JSON file. */
  lazy val NBATeamsPath: String =
    java.nio.file.Paths.get(
      this.getClass.getResource("/nba_teams.json").toURI()
    ).toAbsolutePath().normalize().toString()

  /** Data structures for NBA teams dataset. */
  final case class NBATeam(teamName: String, players: Array[NBAPlayer])
  final case class NBAPlayer(playerName: String, height: Double, weight: Double)

  /** Type alias for the Input of the Average BMI Aggregator. */
  type PlayerInfo = (Double, Double) // (height in meters, weight in kilograms).

  /** Type alias for the Buffer of the Average BMI Aggregator. */
  type AverageBMIBuffer = (Double, Long) // (sum, count).

  /** Type-Safe User-Defined Aggregator to compute the average BMI of a NBATeam. */
  object AverageBMIAggregator extends Aggregator[PlayerInfo, AverageBMIBuffer, Double] {
    override def zero: AverageBMIBuffer = (0.0d, 0l)

    override def reduce(buffer: AverageBMIBuffer, playerInfo: PlayerInfo): AverageBMIBuffer =
      (buffer, playerInfo) match {
        case ((sum, count), (height, weight)) =>
          val bmi = weight / math.pow(height, 2)
          (sum + bmi, count + 1)
      }

    override def merge(b1: AverageBMIBuffer, b2: AverageBMIBuffer): AverageBMIBuffer =
      (b1, b2) match {
        case ((sum1, count1), (sum2, count2)) =>
          (sum1 + sum2, count1 + count2)
      }

    override def finish(reduction: AverageBMIBuffer): Double =
      reduction match {
        case (sum, count) =>
          import math.BigDecimal
          BigDecimal(sum / count).setScale(2, BigDecimal.RoundingMode.HALF_EVEN).toDouble
      }

    override def bufferEncoder: Encoder[AverageBMIBuffer] = Encoders.product

    override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
  }

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
    val lines = sc.parallelize(data) // lines: RDD[String].

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
    println(s"WordCount results: ${wordCount}.")

    println("----------------------------------------------")
    // --------------------------------------------------------------------------------------------

    // ------------------------------------------ DataFrames --------------------------------------
    println("DataFrames -----------------------------------")

    // Read a DataFrame from a CSV file.
    val apartments = // apartments: DataFrame = [name: string, area: double, price double].
      spark
        .read
        .option(key   = "header", value = "true")
        .option(key   = "encoding", value = "UTF-8")
        .option(key   = "sep", value = ",")
        .option(key   = "inferSchema", value = "true")
        .csv(ApartmentsPath)

    // Let's give a look to the data and the inferred schema.
    println("Apartments schema:")
    apartments.printSchema()
    println("Apartments first five rows:")
    apartments.show(numRows  = 5, truncate = false)

    // Compute some basic statistics, like: min, mean & max values, on the data.
    println("Apartments basic statistics:")
    apartments.select($"area", $"price").summary().show(truncate = false)

    // Let's find the apartments whose price per square meter is bellow the mean.
    val apartmentsPSM = // PSM = price per square meter.
      apartments.select(
        $"name", $"area",
        $"price",
        ($"price" / $"area").as("psm")
      )
    val meanPSM =
      apartmentsPSM.select(
        sqlfun.mean($"psm").as("mean-psm")
      ).first().getAs[Double](fieldName = "mean-psm")
    val aparmentsPSMBelowMean =
      apartmentsPSM.where($"psm" < sqlfun.lit(meanPSM)).sort($"psm".asc)

    // Display our results.
    println(s"Mean price per square meter (PSM): ${meanPSM}.")
    println("Apartments whose price per square meter (PSM) is bellow the mean:")
    aparmentsPSMBelowMean.show(truncate = false)

    println("----------------------------------------------")
    // --------------------------------------------------------------------------------------------

    // ------------------------------------------ Datasets ----------------------------------------
    println("Datasets -------------------------------------")

    // Read a Dataset from a JSON file.
    val nbaTeamEncoder: Encoder[NBATeam] = Encoders.product
    val nbaTeamSchema = nbaTeamEncoder.schema
    val nbaTeams = // nbaTeams: Dataset[NBATeam]
      spark
        .read
        .option(key   = "encoding", value = "UTF-8")
        .schema(nbaTeamSchema)
        .json(NBATeamsPath)
        .as[NBATeam](nbaTeamEncoder)

    // Let's give a look to the data and the inferred schema.
    println("NBA teams schema:")
    nbaTeams.printSchema()
    println("NBA teams first five rows:")
    nbaTeams.show(numRows  = 5, truncate = false)

    // Let's compute the average 'Body Mass Index' (BMI) of each team.
    val flattened = for { // flattened: Dataset[String -> PlayerInfor].
      nbaTeam <- nbaTeams // Desugared as flatMap.
      player <- nbaTeam.players // Desugared as map.
    } yield (nbaTeam.teamName -> (player.height, player.weight))
    val groupedByTeam = // groupedByTeam: KeyValueGroupedDataset[String, PlayerInfo].
      flattened
        .groupByKey(_._1) // Group by the player name.
        .mapValues(_._2) // Preserve only the player info in each group.
    val aggregated = // aggregated: Dataset[String -> Double]
      groupedByTeam.agg(AverageBMIAggregator.toColumn.name("average-bmi"))
    val sorted =
      aggregated.sort($"average-bmi".desc)

    // Display our results.
    println("NBA teams sorted by their average body mass index (BMI) descending:")
    sorted.withColumnRenamed(existingName = "value", newName = "team-name").show(truncate = false)

    println("----------------------------------------------")
    // --------------------------------------------------------------------------------------------

    // Stop the Spark Session and exit.
    spark.stop()
  }
}
