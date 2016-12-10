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
import sbt.complete._

class CombineParsersTest extends TestSpec {
  it should "apply either the original parser 'a' or parser 'b' using the combinator '|'" in {
    // A parser that succeeds if the input is "blue" or "green"
    val color: Parser[String] = "blue" | "green"
    color.parse("") should haveFailure("Expected 'blue' ...Expected 'green'")
    color.parse("bluegreen") should haveFailure("Expected end of input. ...bluegreen")
    color.parse("blue green") should haveFailure("Expected end of input. ...blue green")
    color.parse("blue") should beSuccess("blue")
    color.parse("green") should beSuccess("green")
  }

  it should "apply the original parser 'a' and then apply next (in order). The result of both is provides as a pair using the combinator '~'" in {
    // A parser that matches:
    // - "fg" or "bg" (foreground/background)
    // - a space,
    // - and then the color (blue/green)
    //
    // returning the matched values as a ((String, Char), String)
    //
    val select: Parser[String] = "fg" | "bg" // foreground, background
    val color: Parser[String] = "blue" | "green"
    val setColor: Parser[((String, Char), String)] = select ~ SpaceClass ~ color
    setColor.parse("") should haveFailure("Expected 'fg' ...Expected 'bg'")
    setColor.parse("bg blue") should beSuccess(("bg", ' '), "blue")
    setColor.parse("fg green") should beSuccess(("fg", ' '), "green")
  }

  it should "apply the original parser 'a' and then applies next (in order), discarding the result of the original parser using the combinator '~>' or '<~'" in {
    // Often, we don't care about the value matched by a parser, such as the space above
    //  For this, we can use ~> or <~, which keep the result of
    //  the parser on the right (~>) or left (<~), respectively.
    val select: Parser[String] = "fg" | "bg" // foreground, background
    val color: Parser[String] = "blue" | "green"
    val setColor: Parser[(String, String)] = select ~ (' ' ~> color)
    setColor.parse("") should haveFailure("Expected 'fg' ...Expected 'bg'")
    setColor.parse("bg blue") should beSuccess(("bg", "blue"))
    setColor.parse("fg green") should beSuccess(("fg", "green"))
  }

  it should "apply the original parser 'one or more times' and provide the non-empty sequence of results using the combinator '+'" in {
    // Match one or more digits, returning a list of the matched characters
    val digits: Parser[Seq[Char]] = Parser.charClass(_.isDigit, "digit").+
    digits.parse("") should haveFailure("Expected digit")
    digits.parse(" ") should haveFailure("Expected digit")
    digits.parse("abcde") should haveFailure("Expected digit ...abcde")
    digits.parse("abcde abcde") should haveFailure("Expected digit ...abcde abcde")
    digits.parse("123") should beSuccess(Seq('1', '2', '3'))
  }

  it should "apply the original parser zero or more times and provide the (potentially empty) sequence of results using the combinator '*'" in {
    // Match zero or more digits, returning a list of the matched characters
    val digits: Parser[Seq[Char]] = Parser.charClass(_.isDigit, "digit").*
    digits.parse("") should beSuccess(Seq.empty[Char])
    digits.parse(" ") should haveFailure("Expected digit")
    digits.parse("abcde") should haveFailure("Expected digit ...abcde")
    digits.parse("abcde abcde") should haveFailure("Expected digit ...abcde abcde")
    digits.parse("123") should beSuccess(Seq('1', '2', '3'))
  }

  it should "apply the original parser zero or one times, returning None if it was applied zero times or the result wrapped in Some if it was applied once using the combinator '?'" in {
    // Optionally match a digit
    val digit: Parser[Option[Char]] = Parser.charClass(_.isDigit, "digit").?
    digit.parse("") should beSuccess(Option.empty[Char])
    digit.parse(" ") should haveFailure("Expected digit")
    digit.parse("abcde") should haveFailure("Expected digit ...abcde")
    digit.parse("0") should beSuccess(Option('0'))
  }

  it should "apply the original parser to the input and then apply the function 'f' to the result transforming the result to a useful data structure" in {
    // Apply the `digits` parser and then apply the provided function to the matched character sequence to get an Int type
    def toInt(chars: Seq[Char]): Int = chars.mkString.toInt
    val digits: Parser[Seq[Char]] = Parser.charClass(_.isDigit, "digit").+
    val num: Parser[Int] = digits.map(toInt)
    num.parse("") should haveFailure("Expected digit")
    num.parse(" ") should haveFailure("Expected digit")
    num.parse("abcde") should haveFailure("Expected digit ...abcde")
    num.parse("0") should beSuccess(0)
  }

  it should "apply the original parser, but provide a default as the result if the parser fails using the combinator '??'" in {
    // Match a digit character, returning the matched character or return '0' if the input is not a digit
    def toInt(chars: Seq[Char]): Int = chars.mkString.toInt
    val digitWithDefault: Parser[Int] = Parser.charClass(_.isDigit, "digit").+.map(toInt) ?? 0
    digitWithDefault.parse("") should beSuccess(0)
    digitWithDefault.parse(" ") should haveFailure("Expected digit")
    digitWithDefault.parse("abcde") should haveFailure("Expected digit ...abcde")
  }

  it should "apply the original parser, but provide the value as the result if it succeeds using the combinator '^^^'" in {
    // Succeed if the input is "blue" and return the value 4
    val blue: Parser[Int] = "blue" ^^^ 4
    blue.parse("") should haveFailure("Expected 'blue'")
    blue.parse(" ") should haveFailure("Expected 'blue'")
    blue.parse("abcde") should haveFailure("Expected 'blue' ...abcde")
    blue.parse("blue") should beSuccess(4)
  }
}
