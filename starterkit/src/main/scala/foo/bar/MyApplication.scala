package foo.bar

import play.api.libs.json._
import scala.concurrent.{Future, Await, ExecutionContext}
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
  import org.reactivecouchbase.CouchbaseRWImplicits.jsObjectToDocumentWriter

  val driver = ReactiveCouchbaseDriver()
  val bucket = driver.bucket("default")

    // creates a JSON document
  val document = Json.obj(
    "name" -> "John",
    "surname" -> "Doe",
    "age" -> 42
  )

  // persist the JSON doc with the key 'john-doe', using implicit 'jsObjectToDocumentWriter' for serialization
  val setDoc = bucket.set[JsObject]("john-doe", document)

  setDoc.onSuccess {
    case status => println(s"Operation status : ${status.getMessage}")
  }

  // persist the Person instance with the key 'jane-doe', using implicit 'personFmt' for serialization
  val setInstance = bucket.set[Person]("jane-doe", Person("Jane", "Doe", 42))

  setInstance.onSuccess {
    case status => println(s"Operation status : ${status.getMessage}")
  }

  // get the Person instance with the key 'john-doe', using implicit 'personFmt' for deserialization
  val getDoc = bucket.get[Person]("john-doe").map { opt =>
    println(opt.map(person => s"Found John : ${person}").getOrElse("Cannot find object with key 'john-doe'"))
  }

  // delete docs with keys "john-doe" and "jane-doe"
  val deleteDoc = Future.sequence(Seq(bucket.delete("john-doe"), bucket.delete("jane-doe")))

  deleteDoc.onSuccess {
    case statuses => println(s"""Operations status : ${statuses.map(_.getMessage).mkString(", ")}""")
  }

  val futureMessage = for {
    _ <- setDoc
    _ <- setInstance
    message <- getDoc
    _ <- deleteDoc
  } yield message

  futureMessage map { message => println(message) } map { _ => driver.shutdown() }  

}
