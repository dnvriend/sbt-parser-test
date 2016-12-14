import sbt._
import sbt.complete._
import ParserOps._
import scalaz._
import Scalaz._

object UserInput {
  def parse[A](line: String, parser: Parser[A]): ValidationNel[String, A] =
    parser.parse(line)
//    Parser.parse(line, parser).fold(_ => None, success => Option(success))

  def reader[A](parser: Parser[A]): FullReader =
    new sbt.FullReader(None, parser)

  def readLine[A](parser: Parser[A], prompt: String = "> "): Disjunction[String, A] = for {
    line <- reader(parser).readLine(prompt).toRightDisjunction("Could not parse user input")
    result <- parse(line, parser).disjunction.leftMap(_.toList.mkString(","))
  } yield result

}