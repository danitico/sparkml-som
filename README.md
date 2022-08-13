# Spark ML SOM (Self-Organizing Map)

## Notice

**This repository is a fork from [FlorentF9's repository](https://github.com/FlorentF9/sparkml-som)**. The changes applied in this fork are:
- Fixing fit and predict function in order to work correctly with DataFrames
- Bumping the version of Scala from 2.11.8 to 2.12.15
- Bumping the version of Apache Spark from 2.2.0 to 3.2.1

## Description

SparkML-SOM is the only available distributed implementation of Kohonen's Self-Organizing-Map algorithm built on top of Spark ML (the Dataset-based API of Spark MLlib) and fully compatible with Spark versions 2.2.0 and newer. It extends Spark's [`Estimator`](https://github.com/apache/spark/blob/v2.2.0/mllib/src/main/scala/org/apache/spark/ml/Estimator.scala) and [`Model`](https://github.com/apache/spark/blob/v2.2.0/mllib/src/main/scala/org/apache/spark/ml/Model.scala) classes.

* SparkML-SOM can be used as any other MLlib algorithm with a simple `fit` + `transform` syntax
* It is compatible with Datasets/DataFrames
* It can be integrated in a Spark ML Pipeline
* It leverages fast native linear algebra with BLAS

The implemented algorithm is the Kohonen batch algorithm, which is very close to the $k$-means algorithm, but the computation of the average code vector is replaced with a topology-preserving weighted average. For this reason, most of the code is identical to MLlib's $k$-means implementation (see [`org.apache.spark.ml.clustering.KMeans`](https://github.com/apache/spark/blob/v2.2.0/mllib/src/main/scala/org/apache/spark/ml/clustering/KMeans.scala) and [`org.apache.spark.mllib.clustering.KMeans`](https://github.com/apache/spark/blob/v2.2.0/mllib/src/main/scala/org/apache/spark/mllib/clustering/KMeans.scala)).

The same algorithm was implemented by one of my colleagues: https://github.com/TugdualSarazin/spark-clustering (project now maintained by [C4E](https://github.com/Clustering4Ever/Clustering4Ever)).
This version is meant to be simpler to use and more concise, performant and compatible with Spark ML Pipelines and Datasets/DataFrames.

## Quickstart

```scala
import som.SOM

val data: DataFrame = ???

val som = new SOM()
  .setHeight(20)
  .setWidth(20)

val model = som.fit(data)

val summary = model.summary // training summary

val res: DataFrame = summary.predictions
// or predict on another dataset
val res: DataFrame = model.transform(otherData)
```

Retrieve the cost function history to check convergence:

```scala
val cost: Array[Double] = summary.objectiveHistory
println(cost.mkString("[", ",", "]"))
```

...now plot it easily in your favorite visualization tool!

## Parameters

Self-organizing maps essentially depend on their topology, the neighborhood function and the neighborhood radius decay. The algorithm uses a temperature parameter that decays after each iteration and controls the neighborhood radius. It starts at a value $T_{max}$ that should cover the entire map and decreases to a value $T_{min}$ that should cover a single map cell. Here are the configuration parameters:

* **Map grid topology** (`topology`)
  * rectangular _(default)_
* **Height and width**: `height` _(default=10)_, `width`_(default=10)_
* **Neighborhood kernel** (`neighborhoodKernel`)
  * gaussian _(default)_
  * rectangular window
* **Temperature (or radius) decay** (`temperatureDecay`)
  * exponential _(default)_
  * linear
* **Initial and final temperatures**: `tMax` _(default=10.0)_, `tMin` _(default=1.0)_
* **Maximum number of iterations**: `maxIter` _(default=20)_
* **Tolerance (for convergence)**: `tol` _(default=1e-4)_

## Implementation details

The package depends only on spark (core, sql and mllib) and netlib for native linear algebra. It will use native BLAS libraries if possible. Because of classes and methods marked as private in spark, some utility and linear algebra code from spark had to be included into the project: _util.SchemaUtils_, _util.MLUtils_ and _linalg.BLAS_. I kept the original license and tried to keep the code minimal with only the parts needed by SOM.
