
import play.twirl.api._

class hello extends BaseScalaTemplate[play.twirl.api.TxtFormat.Appendable, Format[play.twirl.api.TxtFormat.Appendable]](play.twirl.api.TxtFormat) with play.twirl.api.Template3[String, String, String, play.twirl.api.TxtFormat.Appendable] {

  /**/
  def apply /*1.2*/ (packageName: String, className: String, message: String): play.twirl.api.TxtFormat.Appendable = {
    _display_ {
      {

        Seq[Any](format.raw /*1.59*/ ("""
"""), format.raw /*2.1*/ ("""package """), _display_( /*2.10*/ packageName), format.raw /*2.21*/ ("""

"""), format.raw /*4.1*/ ("""object """), _display_( /*4.9*/ className), format.raw /*4.18*/ (""" """), format.raw /*4.19*/ ("""extends App """), format.raw /*4.31*/ ("""{"""), format.raw /*4.32*/ ("""
    """), format.raw /*5.5*/ ("""println(""""), _display_( /*5.15*/ message), format.raw /*5.22*/ ("""")
"""), format.raw /*6.1*/ ("""}"""))
      }
    }
  }

  def render(packageName: String, className: String, message: String): play.twirl.api.TxtFormat.Appendable = apply(packageName, className, message)

  def f: ((String, String, String) => play.twirl.api.TxtFormat.Appendable) = (packageName, className, message) => apply(packageName, className, message)

  def ref: this.type = this

}

/**/
object hello extends hello
/*
                  -- GENERATED --
                  DATE: Wed Dec 14 14:19:43 CET 2016
                  SOURCE: /Users/dennis/projects/sbt-parser-test/src/main/twirl/org/example/hello.scala.txt
                  HASH: 965c9b29d974ddb1683a3196b6bb8855e462f049
                  MATRIX: 351->1|502->58|529->59|564->68|595->79|623->81|656->89|685->98|713->99|752->111|780->112|811->117|847->127|874->134|903->137
                  LINES: 11->1|16->1|17->2|17->2|17->2|19->4|19->4|19->4|19->4|19->4|19->4|20->5|20->5|20->5|21->6
                  -- GENERATED --
              */
