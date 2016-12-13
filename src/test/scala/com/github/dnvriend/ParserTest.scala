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

  "sbt.complete.DefaultParsers" should "have a 'Space' parser" in {
    //  Matches a non-empty String consisting of whitespace characters.
    sbt.complete.DefaultParsers.Space.parse(" ") should beSuccess(Seq(' '))
    sbt.complete.DefaultParsers.Space.parse("") should haveFailure("Expected whitespace character")
    sbt.complete.DefaultParsers.Space.parse("abcde") should haveFailure("Expected whitespace character ...abcde")
  }

  it should "have a 'SpaceDelimited' parser" in {
    //  Parses a space-delimited, possibly empty sequence of arguments.
    // The arguments may use quotes and escapes according to StringBasic.
    sbt.complete.DefaultParsers.spaceDelimited("<arg>").parse("") should beSuccess(Seq.empty[String])
    sbt.complete.DefaultParsers.spaceDelimited("<arg>").parse(" ") should beSuccess(Seq.empty[String])
    sbt.complete.DefaultParsers.spaceDelimited("<arg>").parse("abcde") should haveFailure("Expected whitespace character ...abcde")
    sbt.complete.DefaultParsers.spaceDelimited("<arg>").parse(" abcde") should beSuccess(Seq("abcde"))
    sbt.complete.DefaultParsers.spaceDelimited("<arg>").parse(" a b c d e") should beSuccess(Seq("a", "b", "c", "d", "e"))
  }

  it should "have a 'SpaceClass' parser" in {
    //  Matches a single whitespace character, as determined by Char.isWhitespace.
    sbt.complete.DefaultParsers.SpaceClass.parse("") should haveFailure("Expected whitespace character")
    sbt.complete.DefaultParsers.SpaceClass.parse("abcde") should haveFailure("Expected whitespace character ...abcde")
    sbt.complete.DefaultParsers.SpaceClass.parse(" ") should beSuccess(' ')
  }

  it should "have a 'NotSpace' parser" in {
    // Matches a non-empty String consisting of non-whitespace characters.
    sbt.complete.DefaultParsers.NotSpace.parse("abcde") should beSuccess("abcde")
    sbt.complete.DefaultParsers.NotSpace.parse("") should haveFailure("Expected non-whitespace character")
    sbt.complete.DefaultParsers.NotSpace.parse("abcde abcde") should haveFailure("Expected non-whitespace character ...abcde abcde")
  }

  it should "have a 'OptSpace' parser" in {
    //  Matches a possibly empty String consisting of whitespace characters.
    sbt.complete.DefaultParsers.OptSpace.parse(" ") should beSuccess(Seq(' '))
    sbt.complete.DefaultParsers.OptSpace.parse("") should beSuccess(Seq.empty[Char])
    sbt.complete.DefaultParsers.OptSpace.parse("abcde") should haveFailure("Expected whitespace character ...abcde")
  }

  it should "have a 'OptNotSpace' parser" in {
    // Matches a possibly empty String consisting of non-whitespace characters.
    sbt.complete.DefaultParsers.OptNotSpace.parse(" ") should haveFailure("Expected non-whitespace character")
    sbt.complete.DefaultParsers.OptNotSpace.parse("") should beSuccess("")
    sbt.complete.DefaultParsers.OptNotSpace.parse("abcde") should beSuccess("abcde")
    sbt.complete.DefaultParsers.OptNotSpace.parse("abcde abcde") should haveFailure("Expected non-whitespace character ...abcde abcde")
  }

  it should "have a 'StringBasic' parser" in {
    // Parses a potentially quoted String value. The value may be verbatim quoted (StringVerbatim),
    // quoted with interpreted escapes (StringEscapable), or unquoted (NotQuoted).
    sbt.complete.DefaultParsers.StringBasic.parse("") shouldBe failure
    sbt.complete.DefaultParsers.StringBasic.parse("'abcde'") should beSuccess("'abcde'")
  }

  it should "have a 'IntBasic' parser" in {
    // Parses a signed integer
    sbt.complete.DefaultParsers.IntBasic.parse("") should haveFailure("Expected digit")
    sbt.complete.DefaultParsers.IntBasic.parse("a") should haveFailure("Expected '-' ...Expected digit ...a")
    sbt.complete.DefaultParsers.IntBasic.parse(Long.MaxValue.toString) should haveFailure("""java.lang.NumberFormatException: For input string: "9223372036854775807" ...9223372036854775807""")
    sbt.complete.DefaultParsers.IntBasic.parse(Long.MinValue.toString) should haveFailure("""java.lang.NumberFormatException: For input string: "-9223372036854775808" ...-9223372036854775808""")
    sbt.complete.DefaultParsers.IntBasic.parse("0") should beSuccess(0)
    sbt.complete.DefaultParsers.IntBasic.parse(Int.MaxValue.toString) should beSuccess(Int.MaxValue)
    sbt.complete.DefaultParsers.IntBasic.parse(Int.MinValue.toString) should beSuccess(Int.MinValue)
  }

  it should "have a 'Digit' parser" in {
    // Parses any single digit and provides that digit as a Char as the result
    sbt.complete.DefaultParsers.Digit.parse("") should haveFailure("Expected digit")
    sbt.complete.DefaultParsers.Digit.parse("-1") should haveFailure("Expected digit ...-1")
    sbt.complete.DefaultParsers.Digit.parse("0") should beSuccess('0')
  }

  it should "have a 'HexDigit' parser" in {
    // Parses a single hexadecimal digit (0-9, a-f, A-F)
    sbt.complete.DefaultParsers.HexDigit.parse("") should haveFailure("Expected hex digit")
    sbt.complete.DefaultParsers.HexDigit.parse("-1") should haveFailure("Expected hex digit ...-1")
    sbt.complete.DefaultParsers.HexDigit.parse("F") should beSuccess('F')
  }

  it should "have a 'Bool' parser" in {
    // Parses the lower-case values true and false into their respesct Boolean values
    sbt.complete.DefaultParsers.Bool.parse("") should haveFailure("Expected 'true' ...Expected 'false'")
    sbt.complete.DefaultParsers.Bool.parse("0") should haveFailure("Expected 'true' ...Expected 'false' ...0")
    sbt.complete.DefaultParsers.Bool.parse("1") should haveFailure("Expected 'true' ...Expected 'false' ...1")
    sbt.complete.DefaultParsers.Bool.parse("on") should haveFailure("Expected 'true' ...Expected 'false' ...on")
    sbt.complete.DefaultParsers.Bool.parse("ON") should haveFailure("Expected 'true' ...Expected 'false' ...ON")
    sbt.complete.DefaultParsers.Bool.parse("off") should haveFailure("Expected 'true' ...Expected 'false' ...off")
    sbt.complete.DefaultParsers.Bool.parse("OFF") should haveFailure("Expected 'true' ...Expected 'false' ...OFF")
    sbt.complete.DefaultParsers.Bool.parse("TRUE") should haveFailure("Expected 'true' ...Expected 'false' ...TRUE")
    sbt.complete.DefaultParsers.Bool.parse("FALSE") should haveFailure("Expected 'true' ...Expected 'false' ...FALSE")
    sbt.complete.DefaultParsers.Bool.parse("true") should beSuccess(true)
    sbt.complete.DefaultParsers.Bool.parse("false") should beSuccess(false)
  }

  it should "have a 'EOF' parser" in {
    sbt.complete.DefaultParsers.EOF.parse("") should beSuccess(())
    sbt.complete.DefaultParsers.EOF.parse("foobar") should haveFailure("Excluded. ...foobar")
  }

  // there are more parsers in the sbt.complete.DefaultParsers object/module
}
