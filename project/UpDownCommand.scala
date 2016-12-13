import sbt._
import sbt.Keys._

trait UpDownCommand

case class UpCommand(version: Int) extends UpDownCommand
case class DownCommand(version: Int) extends UpDownCommand

// if you put the parsers inline
// then you get strange sbt errors
// like Illegal dynamic reference: upDownParser
// seems like a bug: https://github.com/sbt/sbt/issues/1993
object UpDownParser {
  import sbt.complete.DefaultParsers
  import sbt.complete.DefaultParsers._
  import sbt.complete.Parser

  val _migrationNumber: Parser[Int] = DefaultParsers.Digit.+.map(_.mkString.toInt)
  val __migrationNumber: Parser[Int] = Parser.token(DefaultParsers.Digit.+.map(_.mkString.toInt), "<Migration Script Number>")
  val migrationNumber: Parser[Int] =
    Parser.token(DefaultParsers.Digit.+.map(_.mkString.toInt), "<Migration Script Number>")
      .examples(Set("1", "2"), check = true)
  val downParser: Parser[UpDownCommand] = "down" ~> Space ~> migrationNumber.map(DownCommand.apply)
  val upParser: Parser[UpDownCommand] = "up" ~> Space ~> migrationNumber.map(UpCommand.apply)
  val parser: Parser[UpDownCommand] = Space ~> (downParser | upParser)

  // we define an initializer, that way we have access
  // to all the settings of SBT. We will return a Initialize[Parser[T]]
  // and sbt can use that like it was a normal Parser[T] which is great!
  val initializingParser = Def.setting {
    val dir = baseDirectory.value // access to all the settings of sbt
    println("[InitializingParser]: " + dir)
    val setOfMigrationScriptNumbers: Set[String] = {
      // doing some directory processing here :)
      Set("4", "5", "6")
    }

    val migrationNumber: Parser[Int] =
      Parser.token(DefaultParsers.Digit.+.map(_.mkString.toInt), "<Migration Script Number>")
        .examples(setOfMigrationScriptNumbers, check = true)
    val downParser: Parser[UpDownCommand] = "down" ~> Space ~> migrationNumber.map(DownCommand.apply)
    val upParser: Parser[UpDownCommand] = "up" ~> Space ~> migrationNumber.map(UpCommand.apply)
    val parser: Parser[UpDownCommand] = Space ~> (downParser | upParser)
    parser
  }
}