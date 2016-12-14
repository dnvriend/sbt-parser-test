import sbt._
import sbt.Keys._
import sbt.complete._
import sbt.complete.DefaultParsers
import sbt.complete.DefaultParsers._
import ParserOps._
import scalaz._
import Scalaz._

object Greeter {
  final case class Person(name: String, age: Int)

  def parse[A](line: String, parser: Parser[A]): ValidationNel[String, A] =
    parser.parse(line)
//    Parser.parse(line, parser).fold(_ => None, success => Option(success))

  def reader[A](parser: Parser[A]): FullReader =
    new sbt.FullReader(None, parser)

  def readLine[A](parser: Parser[A], prompt: String = "> "): Disjunction[String, A] = for {
    line <- reader(parser).readLine(prompt).toRightDisjunction("Could not parse user input")
    result <- parse(line, parser).disjunction.leftMap(_.toList.mkString(","))
  } yield result

  val nameParser: Parser[String] = token(DefaultParsers.any.*.map(_.mkString), "You can use any character.")
  val ageParser: Parser[Int] = token(DefaultParsers.IntBasic, "Please type only digits ")

  def greet(log: Logger): Option[Greeter.Person] = {
    val validatedPerson: Disjunction[String, Greeter.Person] = for {
      name <- Greeter.readLine[String](Greeter.nameParser, "Please enter your name > ").leftMap(msg => s"Could not parse your name: $msg")
      age <- Greeter.readLine[Int](Greeter.ageParser, "Please enter your age > ").leftMap(msg => s"Could not parse your age: $msg")
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