/**
  * Created by root on 22/12/16.
  */
import org.apache.spark.{ SparkConf, SparkContext }
//import org.apache.spark.SparkContext._

import org.apache.spark.streaming._
//import org.apache.spark.streaming.StreamingContext._

object StreamingApp extends Serializable {
  // schema for sensor data
  case class Sensor(resid: String, date: String, time: String, hz: Double, disp: Double, flo: Double, sedPPM: Double, psi: Double, chlPPM: Double) extends Serializable

  // function to parse line of sensor data into Sensor class

  def parseSensor(str: String): Sensor = {
    val p = str.split(",")
    Sensor(p(0), p(1), p(2), p(3).toDouble, p(4).toDouble, p(5).toDouble, p(6).toDouble, p(7).toDouble, p(8).toDouble)
  }

  val timeout = 10 // Terminate after N seconds
  val batchSeconds = 10 // Size of batch intervals

  def main(args: Array[String]): Unit = {
    // set up able configuration
    val sparkConf = new SparkConf().setAppName("SensorStream").setMaster("local[*]").set("spark.files.overwrite", "true")
    val sc = new SparkContext(sparkConf)

    // create a StreamingContext, the main entry point for all streaming functionality
    val ssc = new StreamingContext(sc, Seconds(batchSeconds))

    // parse the lines of data into sensor objects
    //val textDStream = ssc.textFileStream("/root/Desktop/Spark_Analysis/sensordata.csv")
    val textDStream = ssc.socketTextStream("localhost", 5050)
    textDStream.print()
    val sensorDStream = textDStream.map(parseSensor)

    sensorDStream.foreachRDD { rdd =>

      // There exists at least one element in RDD
      if (!rdd.partitions.isEmpty) {

        // filter sensor data for low psi
        val alertRDD = rdd.filter(sensor => sensor.psi < 5.0)
        println("low pressure alert ")
        alertRDD.take(5).foreach(println)
        alertRDD.saveAsTextFile("/root/Desktop/Spark_Analysis/result1")

      }
    }

    // Start the computation
    println("start streaming")
    ssc.start()
    // Wait for the computation to terminate
    ssc.awaitTermination()

  }

  }
