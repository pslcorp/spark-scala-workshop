# Spark Scala Workshop

Simple Spark with Scala introductory workshop.

This repo is intended to serve as a _"one-oh-one"_ (101) introductory workshop for Spark with Scala.  
It will cover basic aspects of Spark, especially:

  + Spark Execution Model.
  + Resilient Distributed Datasets _(**RDDs**)_.
  + Transformations _(lazy)_ VS Actions _(eager)_.
  + Partitions and Shuffling.
  + Datasets & DataFrames.

## Prerequisites

For this workshop you only need:

  1. A _fork_ of this _repo_.
  2. A [Java Development Kit _(**JDK**)_ 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your classpath.
  3. The [Scala Build Tool _(**SBT**)_](https://www.scala-sbt.org/1.x/docs/Setup.html) installed.
  4. An IDE of your preference.

---

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

You processes your data by applying common _functional_ **transformations**,
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

### _Functional_ Transformations

A simple cheat sheet of the most common _functional_ transformations present in Scala/Spark.  
An Scala **List** will be used in the examples for simplicity.

  + **map:** _Transform an input collection, by applying a transformation function to each element of the collection_.

  ```scala
     def map[A, B](collection: C[A])(f: A => B): C[B]
     List(1, 2, 3).map(x => x + 1) === List(2, 3, 4)
  ```

  + **flatMap:** _Transform an input collection, by applying a transformation function to each element of the collection,
                 and then flatten the results_.

  ```scala
     def flatMap[A, B](collection: C[A])(f: A => C[B]): C[B]
     List(1, 2, 3).flatMap(x => List.fill(x)(x)) === List(1, 2, 2, 3, 3, 3)
  ```

  + **filter:** _Returns the elements of the input collection, that satisfy a given predicate_

  ```scala
     def filter[A](collection: C[A])(p: A => Boolean): C[A]
     List(1, 2, 3).filter(x => (x % 2) != 0) === List(1, 3)
  ```

  + **reduce:** _Reduce all the elements of the input collection, to a single value, using a binary function_

  ```scala
     def reduce[A](collection: C[A])(op: (A, A) => A): A
     List(1, 2, 3).reduce(_ + _) === 6
  ```

  + **fold:** _Like 'reduce', reduce all the elements of the input collection, to a single value, using a binary function.
              But also needs a zero value which allow the operation to change the return type - note: in spark
              this operation is called 'aggregate', and is a little bit more complex_.

  ```scala
     def fold[A, B](collection: C[A])(zero: B)(op: (B, A) => B): B
     List('a', 'b', 'c').foldLeft("")((acc, x) => acc + x) === "abc"
  ```

---

## Datasets & DataFrames

Datasets and DataFrames are higher level abstractions provided by Spark.  
They provide several ways to interact with the data, including **SQL** & the Datasets **API**.

> "They provide Spark with more information about the structure of both the data and the computation being performed.  
> Internally, Spark SQL uses this extra information to perform extra optimizations."

### DataFrames

A DataFrame is a distributed collection of data organized into named columns - It is conceptually equivalent to a table in a relational database.

They allow us to execute SQL queries over our data to express our computations -
commonly used functions can be found on the [**sql.functions**](https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$) object.  
They are incredible optimized, but lack type checking - they use schema to validate the operations, but the schema is only know in runtime.  
However, they're very powerful for data exploratory testing since they can infer the schema from the input data.

### Datasets

A Dataset is a distributed collection of _semi-structured_ typed data - It is somewhat similar to a collection of documents in a NoSQL database.

They are strongly typed and can derive they schema from they their type at runtime.  
However, they require an [**Encoder**](https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.Encoder)
to serialize the objects for processing or transmitting over the network. For that reason, they are commonly used with:
primitive values _(including Strings, Dates & Timestamps)_ and arrays, maps, tuples & case classes of these primitives.  
They can be processed either with _functional_ **transformations** and with SQL queries -
additionally, the Datasets API provides a powerful feature
called [**Aggregators**](https://spark.apache.org/docs/latest/sql-getting-started.html#aggregations),
which allow a simple way to perform parallel aggregations.

> **Note:** A DataFrame is just a `Dataset[Row]`.

---

## Example

In the file **'Introduction.scala'** there are example programs using RDD's, DataFrames and Datasets.

  + **RDD:** Compute the _"Word Count"_ over a text.
  + **DataFrames:** _"Data Exploratory Analysis"_ over a CSV file.
  + **Datasets:** _"Aggregation"_ over a JSON file.

You can run the examples by executing the following command.

  ```bash
     $ sbt "run co.com.psl.training.spark.Introduction"
  ```

---

## Exercise

In the file **'Exercise.scala'** there is the code template for solve an exercise to apply what has been learned.

We have a record of the locations in which the users have been, the record saves
the id of the user, the date _(with precision in minutes)_ and the registered location.  
The exercise consists of obtaining the list of locations _(without repeated values)_ in which each user was in each hour.

> **Note:** you may change the signature of the function to use the abstraction, _(RDD, DataFrame or Dataset)_, you prefer.

You can run the exercise by executing the following command.

  ```bash
     $ sbt "run co.com.psl.training.spark.Exercise"
  ```

The output of your program must look like this.

  | userid    | datetime            | locations        |
  | :-------: | :-----------------: | ---------------- |
  | "lmejias" | 2018-12-31T13:00:00 | ["home", "work"] |
  | "lmejias" | 2018-12-31T15:00:00 | ["cc"]           |
  | "dossass" | 2018-12-31T13:00:00 | ["home", "cc"]   |
  | "dossass" | 2018-12-31T17:00:00 | ["home", "cc"]   |

---
