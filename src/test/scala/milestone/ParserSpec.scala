package com.banno.milestone

import org.specs2.mutable.Specification
import com.banno.milestone.Parser._
import http._
import com.banno.Entities.Classes._

object ParserSpec extends Specification {

  val returnString = """\">Scala (programming language)</a>A general-purpose programming language providing support for functional programming and a strong...""""

// generateURL
  "When we pass the string Scala generateURL" should {
    "return a url" in {
      generateURL("Scala").get == "https://api.duckduckgo.com/?q=Scala&format=json"
    }
  }

// \">Scala (programming language)</a>A general-purpose programming language providing support for functional programming and a strong..."
// useRegex
  "when we pass in an unparsed String useRegex" should {
    "return a list of parsed strings" in {
      useRegex(Http4sHttpClient.executeHttpGet("https://api.duckduckgo.com/?q=scala&format=json").body).get(0) == returnString
    }
  }

  "if we were to pass a string that didn't have anything that satisfied the regex getRegex" should {
    "return an empty list" in {
      useRegex("blah blah blah") == Some(List())
    }
  }

// createResult
  "If we pass a string we got from useRegex createResult" should {
    "return a Result" in {
      createResult(returnString).get == Result("Scala (programming language)", "A general-purpose programming language providing support for functional programming and a strong...")
    }
  }

  "If we pass a string that doesn't match the requirements createResult" should {
    "throw an exception" in {
      createResult("lkjsfl;kjas;lfkas") == None
    }
  }

// createResultList
  "If we use Scala as the searchTerm createResultList" should {
    "return a list of Results" in {
      createResultList("Scala").isInstanceOf[List[Result]]
    }
  }
}
