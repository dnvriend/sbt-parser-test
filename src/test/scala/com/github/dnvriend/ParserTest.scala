/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend

import com.github.dnvriend.ParserOps._
import sbt.complete.DefaultParsers._
import sbt.complete.Parser

// https://github.com/ktoso/sbt-bash-completion
// https://github.com/sbt/completion-example/blob/master/Main.scala
// http://www.scala-sbt.org/0.13/docs/Parsing-Input.html
// see: http://labs.unacast.com/2016/05/19/sbt-based-commandline-synonym-lookup/
// see: https://github.com/unacast/sbt-cli-example/blob/master/src/main/scala/commands.scala
class ParserTest extends TestSpec {
  //  val hi: Parser[String] = token("hi")
  //  val help: Parser[String] = token("help")
  //  val exit: Parser[String] = token("exit")

  "Parsers" should "match a single character" in {
    // A parser that succeeds if the input is 'x', returning the Char 'x' and failing otherwise
    val singleChar: Parser[Char] = 'a'
    singleChar.parse("") should haveFailure("Expected 'a'")
    singleChar.parse("a") should beSuccess('a')

    val matchBlue: Parser[String] = "blue"
    matchBlue.parse("") should haveFailure("Expected 'blue'")
    matchBlue.parse("blue") should beSuccess("blue")
  }

  it should "have parser constructors" in {
    // A parser that succeeds if the character is a digit, returning the matched Char
    //   The second argument, "digit", describes the parser and is used in error messages
    val digit: Parser[Char] = charClass((c: Char) => c.isDigit, "digit")
    digit.parse("") should haveFailure("Expected digit")
    digit.parse("a") should haveFailure("Expected digit ...a")
    digit.parse("1") should beSuccess('1')

    // A parser that produces the value 3 for an empty input string, fails otherwise
    val alwaysSucceed: Parser[Int] = Parser.success(3)
    alwaysSucceed.parse("") should beSuccess(3)
    alwaysSucceed.parse("33") should haveFailure("Expected end of input. ...33")

    // Represents failure (always returns None for an input String).
    // The argument is the error message.
    intercept[java.lang.StringIndexOutOfBoundsException] {
      // something wrong in the internals of sbt...
      val alwaysFail: Parser[Nothing] = Parser.failure("Invalid input.")
      alwaysFail.parse("foo") should haveFailure("")
    }
  }
}
