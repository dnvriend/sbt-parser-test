# sbt-parser-test
A small study project on SBT's 'parser combinators'.

## Introduction
SBT parser combinators or 'parsers' for short are typically used to parse user input and provide tab completion for Input Tasks and Commands.
In functional programming, a parser combinator is a higher-order function that accepts several parsers as input and returns a new parser
as its output, thats exactly what SBT parsers do.

To make sense of text, you need a way to parse it, and parser combinator libraries such as [sbt parsers](), 
[fastparse](https://github.com/lihaoyi/fastparse) or [scala parser combinator](https://github.com/scala/scala-parser-combinators) 
can help you parse arbitrary text.

Running the (parser) function will result in traversing the stream of characters, 
yielding a value that represents the AST for the parsed expression, 
or failing with a parse error for malformed input, 
or failing by not consuming the entire stream of input. 

A more robust implementation would track the position information of failures for error reporting.





