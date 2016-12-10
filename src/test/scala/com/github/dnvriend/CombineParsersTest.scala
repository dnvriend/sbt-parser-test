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

  it should "apply the original parser 'a' and then apply next (in order). The result of both is provides as a pair" in {
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

  it should "apply the original parser 'a' and then applies next (in order), discarding the result of the original parser. (The arrow point in the direction of the retained result)" in {
    // Often, we don't care about the value matched by a parser, such as the space above
    //  For this, we can use ~> or <~, which keep the result of
    //  the parser on the right or left, respectively
    val select: Parser[String] = "fg" | "bg" // foreground, background
    val color: Parser[String] = "blue" | "green"
    val setColor: Parser[(String, String)] = select ~ (' ' ~> color)
    setColor.parse("") should haveFailure("Expected 'fg' ...Expected 'bg'")
    setColor.parse("bg blue") should beSuccess(("bg", "blue"))
    setColor.parse("fg green") should beSuccess(("fg", "green"))
  }
}
