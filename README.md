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





