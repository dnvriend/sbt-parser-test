import sbt._
import sbt.complete._
import sbt.complete.DefaultParsers
import sbt.complete.DefaultParsers._
import scalaz._
import scala.util.Try

object Scaffold {
  final case class HelloWorldScaffold(packageName: String, className: String, message: String)

  val packageParser: Parser[String] = token(DefaultParsers.any.*.map(_.mkString), "You can use any character.").examples("com.github.dnvriend")
  val classNameParser: Parser[String] = token(DefaultParsers.any.*.map(_.mkString), "You can use any character.").examples("HelloWorld")
  val messageParser: Parser[String] = token(DefaultParsers.any.*.map(_.mkString), "You can use any character.").examples("Hello World!")

  def getFileName(scaffold: HelloWorldScaffold, sourceDirectory: File): File =
    sourceDirectory / "main" / "scala" / scaffold.packageName / s"${scaffold.className}.scala"

  def scaffold(log: Logger, baseDirectory: File, sourceDirectory: File) = {
    val maybeScaffold: Disjunction[String, HelloWorldScaffold] = for {
      packageName <- UserInput.readLine[String](Scaffold.packageParser, "Enter package name > ").leftMap(msg => s"Could not parse package: $msg")
      className <- UserInput.readLine[String](Scaffold.classNameParser, "Enter className > ").leftMap(msg => s"Could not parse className: $msg")
      message <- UserInput.readLine[String](Scaffold.messageParser, "Enter message to output > ").leftMap(msg => s"Could not parse message: $msg")
    } yield HelloWorldScaffold(packageName, className, message)

    maybeScaffold match {
      case DRight(scaffold) =>
        val content = hello.render(scaffold.packageName, scaffold.className, scaffold.message)
        log.info(s"[Scaffold]: '$content'")
        Try(IO.write(getFileName(scaffold, sourceDirectory), content.toString)).recover { case t: Throwable =>
          log.error(s"Could not write scaffold: $t")
        }
      case DLeft(message) =>
        log.warn(s"Oops: $message")
    }
    maybeScaffold.toOption.map(_.toString)
  }
}