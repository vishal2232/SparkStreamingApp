/**
  * Created by root on 21/12/16.
  */
import org.apache.spark.{ SparkContext, SparkConf }
object wordcount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Test")
      .set("spark.executor.memory", "2g")
    val sc = new SparkContext(conf)
    //val lines = sc.parallelize(Seq("This is a word count program", "to count the lines", "and reduce the lines ."))
    val  path = sc.textFile("/root/Desktop/installations.txt")
    val flatLines = path.flatMap(_.split(" "))
    val mappingcount = flatLines.map(rec=>(rec,1))
    val reduced = mappingcount.reduceByKey(_+_)
    reduced.foreach(println)
  }

}
