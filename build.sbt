name := "Spark-Scala-Workshp"
organization := "co.com.psl"
organizationName := "PSL S.A.S."
organizationHomepage := Some(url("http://www.psl.com.co/"))
version := "0.1.0"
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

val sparkVersion = "2.4.0"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)

// Execute run in a separate JVM.
fork in run := true

// Hack for getting the Provided dependencies work in the run task.
fullClasspath in Runtime := (fullClasspath in Compile).value
run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
runMain in Compile := Defaults.runMainTask(fullClasspath in Compile, runner in(Compile, run)).evaluated
