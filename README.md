# sbt-parser-test
A small study project on SBT's 'parser combinators'.

## Introduction
SBT parser combinators or 'parsers' for short are typically used to parse user input and provide tab completion for Input Tasks and Commands
and this is exactly what SBT parsers do.

In functional programming, a parser combinator is a higher-order function that accepts several parsers as input
and returns a new parser as its output. In this context, a parser is a function accepting strings as input
and returning some structure as output, typically a parse tree or a set of indices representing locations in the string
where parsing stopped successfully.

Parser combinators enable a recursive descent parsing strategy
that facilitates modular piecewise construction and testing.

This parsing technique is called combinatory parsing.

To make sense of text, you need a way to parse it, and parser combinator libraries such as [sbt parsers](), 
[fastparse](https://github.com/lihaoyi/fastparse) or [scala parser combinator](https://github.com/scala/scala-parser-combinators) 
can help you parse arbitrary text.

Running the (parser) function will result in traversing the stream of characters, 
yielding a value that represents the AST for the parsed expression, 
or failing with a parse error for malformed input, 
or failing by not consuming the entire stream of input. 

A more robust implementation would track the position information of failures for error reporting.

Parsers are able to take an input stream and attempt to see if that string matches their expectations.
If it does, the parser return a value, denoted in its type

## Parser types
Outside of SBT, so in any Scala project you can use the `Parser[T]` type to parse user input, but if you
parse user input in SBT then you have more options:

- __Parser[I]__: a basic parser that can be used outside SBT and does not use any settings,
- __Initialize[Parser[I]]__: a parser whose definition depends on one or more settings
- __Initialize[State => Parser[I]]__: a parser that is defined using both settings and the current state.

## DefaultParsers
The sbt completion library provides a core set of parsers within the ` sbt.complete.DefaultParsers` object
that you can use to build up more-complex parsers.

## Input Tasks
An input task is a task that _can_ accept additional user input before execution. It provides the interaction
between the user (input) and the build tool and can provide context-sensitive help via tab completion.

Input tasks do the following:

- Parse user input,
- Produce a task to run.

Parsing Input describes how to use the parser combinators that define the input syntax and tab completion.

This page describes how to hook those parser combinators into the input task system.

...

## Input Keys
SBT defines a number of keys that return values. That is the reason why these key types exist in SBT. The most simple to understand and to use is [SettingKey](http://www.scala-sbt.org/0.13.12/api/index.html#sbt.SettingKey)
that define settings which are values that will be evaluated only once, when SBT is launched __or__ when you alter the setting eg. on the sbt console with the
__set__ command. The next one is the [TaskKey](http://www.scala-sbt.org/0.13.12/api/index.html#sbt.TaskKey) that also returns a value but will be evaluated on demand,
and exist to be executed repeatedly.

Another key type is the [InputKey](http://www.scala-sbt.org/0.13.12/api/index.html#sbt.InputKey) that also returns a value
and also exists to be executed repeatedly, and in that sense is similar to the TaskKey, but what makes the InputKey special is that
it can parse user input. The reason the InputKey can parse user input is that the user input is passed into the InputKey implicitly.
Lets look at a working example:

```scala
// A key for a setting is of type SettingKey and represents a Setting
val settingDemo: SettingKey[String] = settingKey[String]("Simple setting")

// we can use multiple styles to define a
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
  //
  // all the text you type after 'demo' in the sbt console, is called 'the user input'.
  // the user input will be implicitly passed to this definition.
  // (everything between // the brackets is alled the 'definition')
  // and will be made available to the parser that we choose to use; here we use
  // a simple, sort-of 'HelloWorld' parser the spaceDelimited parser.
  //
  val _userInput: Seq[String] = DefaultParsers.spaceDelimited("<arg>").parsed
  val userInput: Seq[String] = Def.spaceDelimited().parsed // does the same as the line above
  println("[InputKeyDemo]: The arguments to demo were:")
  userInput.foreach(println)
}.evaluated
```

We first create an InputKey that can be 'started' in the sbt console by typing 'demo'. The `DefaultParsers.spaceDelimited`
is a handy method for parsing spacedelimited input and allows for a tab-completion message to be shown when you press the
tab key. Here only the message "<arg>" will be shown on the console. When you press the enter key the user input will be parsed
by the parser and the spaceDelimited parser will return the parsed input as a Seq[String], and the contents of that sequence
will be shown on the console.

## A note about user input
We define user input as 'whatever the user enters __right after__ the name of the key which could be 'inputKeyDemo'. So
for example if you have an inputKey with the name 'inputKeyDemo' and you type eg. 'inputKeyDemo a b c' then the
inputTask will receive the following user input: __' a b c'__,  _note the starting space at the beginning_.

So the user input is everything typed right after the name of the inputKey, which always will begin with a space.

User input parsers must therefor always deal with this starting space, that is why the `DefaultParsers.spaceDelimited`
parser for example starts parsing user input starting with a Space.

## Calling InputTask
The types involved in an input task are composable, so it is possible to reuse input tasks.

The `.parsed` and `.evaluated` methods are defined on InputTasks to make this more convenient in common situations:

- Call `.parsed` on an `InputTask[T]` or `Initialize[InputTask[T]]` to get the `Task[T]`
  created after parsing the command line
- Call `.evaluated` on an `InputTask[T]` or `Initialize[InputTask[T]]` to get the value of type `T`
  from evaluating that task

In both situations, the underlying Parser is sequenced with other parsers in the input task definition.
In the case of `.evaluated`, the generated task is evaluated.

## Tab completion
The sbt console comes with baked-in autocomplete options when typing commands. For users to see what possible things they can type,
they hit Tab, and sbt will attempt to provide possible completions. Autocomplete or tab-completion can really streamline a workflow
when used correctly. Tab completion is all about showing contextual help message to be shown when you press the
tab key

The `Parser.token` method takes an existing parser and gives it a label for the user. The main purpose of token is
to determine the boundaries for suggestions.


## Notes
- http://stackoverflow.com/questions/34162484/illegal-dynamic-reference

you'll need to define your work in a dynamic task http://www.scala-sbt.org/0.13/docs/Tasks.html#Dynamic+Computations+with which allows you to define your Task's dependencies based on things that are not well-defined at compile time.

Remember, in sbt all tasks are really a map from their dependencies to the result and any time your type thing.value you're really writing (thing).map { valueOfThing => ... } once the macro has its wicked way.

- http://www.scala-sbt.org/0.13/docs/Custom-Settings.html
- https://github.com/sbt/sbt/issues/1993
- http://www.scala-sbt.org/1.0/docs/Plugins-Best-Practices.html
- https://github.com/sbt/sbt/issues/202
- http://eed3si9n.com/4th-dimension-with-sbt-013
- http://stackoverflow.com/questions/28574678/prompt-for-user-input-when-running-scala-program-with-sbt
- http://stackoverflow.com/questions/25632113/sbt-how-to-pass-input-from-one-inputtask-to-another-inputtask
- http://www.scala-sbt.org/0.13/docs/Howto-After-Input-Task.html
- http://www.ethanjoachimeldridge.info/tech-blog/how-to-create-sbt-task-that-takes-an-argument
- http://stackoverflow.com/questions/28574678/prompt-for-user-input-when-running-scala-program-with-sbt
- http://alvinalexander.com/scala/scala-shell-scripts-how-prompt-users-input-read
- http://alvinalexander.com/scala/scala-shell-scripts-command-line-prompting-user-output-reading-input-console
- http://www.scala-sbt.org/0.13/docs/Console-Project.html
- http://www.scala-sbt.org/0.13/docs/Build-State.html
- [sbt issue 2278 - Allow scripted tests to simulate user input to prompts](https://github.com/sbt/sbt/issues/2278)
- [sbt - alter shell prompt - Interactive Mode](http://www.scala-sbt.org/0.13/docs/Howto-Interactive-Mode.html)
- [Sbt based command-line parser](http://labs.unacast.com/2016/05/19/sbt-based-commandline-synonym-lookup/)
- https://www.lightbend.com/partners/become-a-partner
- JLine keybindings
- https://www.playframework.com/documentation/2.5.x/ScalaCustomTemplateFormat
- https://github.com/playframework/twirl
- https://github.com/spray/twirl
- https://www.playframework.com/documentation/2.5.x/ScalaTemplates
- https://www.playframework.com/documentation/2.5.x/ScalaCustomTemplateFormat

## Twirl
Twirl template files are expected to be placed under `src/main/twirl` or `src/test/twirl`, similar to scala or java sources.
The source locations for template files can be configured.

Template files must be named `{name}.scala.{ext}` where ext can be html, js, xml, or txt.

The Twirl template compiler is automatically added as a source generator for both the main/compile and test configurations.
When you run compile or test:compile the Twirl compiler will generate Scala source files from the templates and then these 
Scala sources will be compiled along with the rest of your project.
