import sbt.Scoped.ScopingSetting

name := "sbt-parser-test"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.10.6"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.7"
libraryDependencies += "org.scala-sbt" % "completion" % "0.13.13"
libraryDependencies += "org.scala-sbt" % "main-settings" % "0.13.13"

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

////
// InputKeys
////
import sbt.complete.DefaultParsers
import sbt.complete.DefaultParsers._
import sbt.complete.Parser

// A key for a setting is of type SettingKey and represents a Setting
val settingDemo2 = settingKey[String]("a setting")
// we use the 'initialization' style here to define the setting, which is just a block of code
// that must return a value and can use other settings to do so.
// Implicitly, an appropriate initializer will be created and assigned to the key 'settingDemo2'
settingDemo2 := {
  "foo"
}

// A key for a setting is of type SettingKey and represents a Setting
val settingDemo: SettingKey[String] = settingKey[String]("Simple setting")

// here we use the explicit 'setting' initializer using 'Def.setting'.
settingDemo := Def.setting {
  "Hello World!"
}.value

// A key for a Task is of type TaskKey and represents a Task
val taskDemo: TaskKey[Unit] = taskKey[Unit]("simple 'Hello World' task")

taskDemo := Def.task {
  println("[TaskDemo]: " + settingDemo.value)
}.value

// A key for an inputTask is of type InputKey and represents an InputTask
val inputKeyDemo: InputKey[Unit] = inputKey[Unit]("demo input key that accepts user input separated by spaces")

inputKeyDemo := Def.inputTask {
  val _userInput: Seq[String] = DefaultParsers.spaceDelimited("<arg>").parsed
  val userInput: Seq[String] = Def.spaceDelimited().parsed // does the same as the line above
  println("[InputKeyDemo]: The arguments to demo were:")
  userInput.foreach(println)
}.evaluated

// parsing user input with an up/down command
val upDownCmd: InputKey[UpDownCommand] = inputKey[UpDownCommand]("demo parsing user input and returning an UpDownCommand")

upDownCmd := Def.inputTask {
//  val _result = UpDownParser.parser.parsed
  val result = UpDownParser.initializingParser.parsed
  println("Executing command: " + result)
  result
}.evaluated