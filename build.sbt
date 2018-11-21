name := "Spark-Scala-Workshp"
organization := "co.com.psl"
organizationName := "PSL S.A.S."
organizationHomepage := Some(url("http://www.psl.com.co/"))
version := "1.0.0"
scalaVersion := "2.12.7"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-Xlint:infer-any",
  "-Xlint:unsound-match",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any"
)

val SparkVersion = "2.4.0"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % SparkVersion,
  "org.apache.spark" %% "spark-sql"  % SparkVersion
)

// Allow to stop run with CTRL + C.
fork in run := true
cancelable in Global := true
