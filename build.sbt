name := "sparkml-som"

version := "0.2.1"

scalaVersion := "2.12.15"

val sparkVersion = "3.2.1"

libraryDependencies ++= Seq(
  "org.apache.spark"         %% "spark-core"  % sparkVersion % "provided",
  "org.apache.spark"         %% "spark-sql"   % sparkVersion % "provided",
  "org.apache.spark"         %% "spark-mllib" % sparkVersion % "provided"
)
