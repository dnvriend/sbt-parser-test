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

import sbt.complete.Parser

import scalaz._
import Scalaz._

object ParserOps {
  implicit class ParserImplicits[T](val that: Parser[T]) extends AnyVal {
    def parse(input: String): ValidationNel[String, T] = Parser.parse(input, that).validationNel
  }

  implicit class EitherImplicits[T](val that: Either[String, T]) extends AnyVal {
    def cleanup(input: String): Option[String] =
      Option(input.replaceAll("\\n", "").replaceAll("\\t", "").replace("^", "").trim).filter(_.nonEmpty)
    def validationNel: ValidationNel[String, T] = that.validation.leftMap { str =>
      str.split("\\n").toList.flatMap(cleanup).mkString(" ...").wrapNel
    }
  }
}
