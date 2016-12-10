name := "sbt-parser-test"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.10.6"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.7"
libraryDependencies += "org.scala-sbt" % "completion" % "0.13.13"

// testing
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test  
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1"

fork in Test := true
parallelExecution := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

// enable scala code formatting //
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

// Scalariform settings
SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

// enable updating file headers //
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

enablePlugins(AutomateHeaderPlugin, SbtScalariform)
