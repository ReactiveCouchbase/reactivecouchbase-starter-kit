package foo.bar

import play.api.libs.json._
import scala.concurrent.{Await, ExecutionContext}
import org.reactivecouchbase.ReactiveCouchbaseDriver
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

case class Person(name: String, surname: String, age: Int)

object Utils {
  implicit val personFmt = Json.format[Person]
  implicit val ec = ExecutionContext.Implicits.global
  val timeout = Duration(10, TimeUnit.SECONDS)
}

object MyApplication extends App {

  import Utils._

  val driver = ReactiveCouchbaseDriver()
  val bucket = driver.bucket("default")

    // creates a JSON document
  val document = Json.obj(
    "name" -> "John",
    "surname" -> "Doe",
    "age" -> 42
  )

  for (i <- 17 to 100) {
    bucket.set(s"person-$i", Person("Jane", "Doe", i))
  }

  for (i <- 1 to 100) {
    Await.result(bucket.delete(s"person-$i"), timeout)
  }

  driver.shutdown()

}
