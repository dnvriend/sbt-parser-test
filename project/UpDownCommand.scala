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

  val migrationNumber = DefaultParsers.Digit.+.map(_.mkString.toInt)
  val downParser = "down" ~> Space ~> migrationNumber.map(DownCommand.apply)
  val upParser = "up" ~> Space ~> migrationNumber.map(UpCommand.apply)
  val parser = Space ~> (downParser | upParser)
}