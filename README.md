# Spark Scala Workshop

Simple Spark with Scala introductory workshop.

This repo is intended to serve as a _"one-oh-one"_ (101) introductory workshop for Spark with Scala.  
It will cover basic aspects of Spark, especially:

  + Spark Execution Model.
  + Resilient Distributed Datasets _(**RDDs**)_.
  + Transformations _(lazy)_ VS Actions _(eager)_.
  + Partitions and Shuffling.
  + DataFrames & Datasets.

## Prerequisites

For this workshop you only need:

  1. A _fork_ of this _repo_.
  2. A [Java Development Kit _(**JDK**)_ 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your classpath.
  3. The [Scala Build Tool _(**SBT**)_](https://www.scala-sbt.org/1.x/docs/Setup.html) installed.
  4. An IDE of your preference.

## Spark Execution Model

> Spark is a "unified analytics engine for large-scale data processing".

That basically means that it is intended for processing datasets that
won't fit on a single machine memory, and thus have to be partitioned
across many machines, implying a distributed processing.

To accomplish this, spark uses a basic 1-Master & N-Slaves cluster architecture.  
In Spark, the _master_ is called the **Driver**, which holds the `SparkContext`,
and the _slaves_ are called **Executors**, which are responsible of keeping
the `Data` and running `Tasks` to process it. The `SparkContext` is in charge of
communicating with a **Cluster Manager** to allocate resources for the **Executors**,
and it sends the application code from the **Driver** to the **Executors**.

![Spark-Architecture](https://spark.apache.org/docs/latest/img/cluster-overview.png)

> Spark, Cluster Mode: https://spark.apache.org/docs/latest/cluster-overview.html
