import scalaz._
import Scalaz._
import sbt.complete.Parser

object ParserOps {
  implicit class ParserImplicits[T](val that: Parser[T]) extends AnyVal {
    def parse(input: String): ValidationNel[String, T] = Parser.parse(input, that).validationNel
  }

  implicit class EitherImplicits[T](val that: Either[String, T]) extends AnyVal {
    def cleanup(input: String): Option[String] =
      Option(input.replaceAll("\\n", "").replaceAll("\\t", "").replace("^", "").trim).filter(_.nonEmpty)
    def validationNel: ValidationNel[String, T] = that.validation.leftMap { str =>
      str.split("\\n").toList.flatMap(cleanup).mkString(" ...").wrapNel
    }
  }
}
