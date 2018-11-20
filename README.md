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

## Resilient Distributed Datasets _(**RDDs**)_.

`RDDs` are at the very core of Spark, they are the main and lower level abstraction
provided by the framework.  
`RDDs` can be seen as regular Scala _collection_ of
elements - with the particularity of being partitioned across the nodes of the cluster
and can be operated in parallel. An `RDD` can be created from `parallelizing` an
existing Scala _collection_, or by reading an external dataset.

You processes your data by applying common _"functional"_ **transformations**,
like `map`, `flatMap`, `filter`, `reduce`, _etc_; over your `RDDs`.  
`RDDs` are **immutable**, that means when you apply one of these
**transformations**, you get back a new `RDD`.  
Also, `RDDs` **transformations** are **lazy**, that means they don't execute
anything when called; but instead, they create **graph** for representing
your program, which will be run latter.

There is another kind of operations supported by `RDDs`, that return plain scala values,
instead of `RDDs`. These are called **actions** and are **eager**.  
That means they run the program's graph, by executing each of the planned
**transformations**, to compute their result value.

![Saprk-DAG](https://image.slidesharecdn.com/sparkinternalsworkshoplatest-160303190243/95/apache-spark-in-depth-core-concepts-architecture-internals-12-638.jpg?cb=1457597704)

> Apache Spark in Depth: Core Concepts, Architecture & Internals: https://www.slideshare.net/akirillov/spark-workshop-internals-architecture-and-coding-59035491

### Example

On the file **'Introduction.scala'** there is an example program using RDD's
to compute the _"Word Count"_ over a text.  
You run the program by executing the following command.

```bash
 $ sbt "run co.com.psl.training.spark.Introduction"
```
