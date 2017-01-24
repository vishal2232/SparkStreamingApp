/**
  * Created by root on 22/12/16.
  */
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext

object wordCountStreaming {
  def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("StremingWordCount").setMaster("local[2]")
    //val ssc = new StreamingContext(conf, Seconds(10) )
    val ssc = new StreamingContext(args(0), "wordcount", Seconds(20))
    //val data = ssc.socketTextStream("localhost", 5050.toInt)      // cat data.txt | ncat -l -p 80 || nc -lp portnumber
    val data = ssc.socketTextStream(args(1),args(2).toInt)
    val wc = data.flatMap(_.split(" ")).map(word=>(word,1)).reduceByKey(_+_)
    wc.print()
    ssc.start()
    ssc.awaitTermination()
    }

}
