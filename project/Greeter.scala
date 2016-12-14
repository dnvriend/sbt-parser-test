import sbt._
import sbt.complete._
import sbt.complete.DefaultParsers
import sbt.complete.DefaultParsers._
import scalaz._

object Greeter {
  final case class Person(name: String, age: Int)

  val nameParser: Parser[String] = token(DefaultParsers.any.*.map(_.mkString), "You can use any character.")
  val ageParser: Parser[Int] = token(DefaultParsers.IntBasic, "Please type only digits ")

  def greet(log: Logger): Option[Greeter.Person] = {
    val validatedPerson: Disjunction[String, Greeter.Person] = for {
      name <- UserInput.readLine[String](Greeter.nameParser, "Please enter your name > ").leftMap(msg => s"Could not parse your name: $msg")
      age <- UserInput.readLine[Int](Greeter.ageParser, "Please enter your age > ").leftMap(msg => s"Could not parse your age: $msg")
    } yield Greeter.Person(name, age)

    validatedPerson match {
      case DRight(person) =>
        println(s"[Greeting]: Hello: $person")
      case DLeft(message) =>
        println(s"Oops: $message")
    }

    validatedPerson.toOption
  }
}