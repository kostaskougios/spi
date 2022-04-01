package com.aktit.wikipedia.dto

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios 17/05/18 - 15:53
  */
class RevisionTest extends AnyFunSuite {
  test("breakToWords splits words") {
    WikipediaBuilder.revision(text = "hello world").breakToWords should be(Seq("hello", "world"))
  }

  test("breakToWords ignores dots") {
    WikipediaBuilder.revision(text = "hello world.").breakToWords should be(Seq("hello", "world"))
  }

  test("breakToWords ignores !") {
    WikipediaBuilder.revision(text = "hello world!").breakToWords should be(Seq("hello", "world"))
  }

  test("breakToWords ignores newline") {
    WikipediaBuilder.revision(text = "hello\nworld").breakToWords should be(Seq("hello", "world"))
  }

  test("breakToWords complex") {
    WikipediaBuilder.revision(text = "sofia, [malgré] tout aimait: :la laitue et le choux! dot.com @ $ % ^ & * ( ) - + =").breakToWords should be(
      Seq(
        "sofia",
        "malgré",
        "tout",
        "aimait",
        "la",
        "laitue",
        "et",
        "le",
        "choux",
        "dot",
        "com"
      )
    )
  }

}
