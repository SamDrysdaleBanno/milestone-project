package http

import org.specs2.mutable.Specification

import java.io.IOException
import java.util.ArrayList

import org.http4s.client.blaze._
import cats.effect.IO
import org.http4s.client._

object HttpClientSpec extends Specification {

// executeHttpGet
  "When we call a Get request executeHttpGet" should {
    "return a status code of 200" in {
      Http4sHttpClient.executeHttpGet("https://duckduckgo.com").statusCode === 200
    }
  }

  "When we call a Get request with invalid link executeHttpGet" should {
    "throw a 301 error" in {
      Http4sHttpClient.executeHttpGet("htsdfstps://duckduckgo.com").statusCode === 301
    }
  }

  "When we call a Get request executeHttpGet" should {
    "return a header" in {
      Http4sHttpClient.executeHttpGet("https://duckduckgo.com").header != null
    }
  }

  "When we call a Get request executeHttpGet" should {
    "return a HttpResponse" in {
      Http4sHttpClient.executeHttpGet("https://duckduckgo.com") must haveClass[HttpResponse]
    }
  }

/*
// executeHttpPost

  "When we call a Get request executeHttpPost" should {
    "return a HttpResponse" in {
      Http4sHttpClient.executeHttpPost("https://duckduckgo.com", Map("Alabama" -> "1", "Iowa" -> "2")) must haveClass[HttpResponse]
    }
  }

  "When we call a Post request executeHttpPost" should {
    "return a status code of 200" in {
      Http4sHttpClient.executeHttpPost("https://duckduckgo.com", Map("Alabama" -> "1", "Iowa" -> "2")).statusCode == 200
    }
  }

  "When we call a Post request with an invalid link executeHttpPost" should {
    "throw an exception" in {
      Http4sHttpClient.executeHttpPost("htsdfdstps://duckduckgo.com", Map("Alabama" -> "1", "Iowa" -> "2")) must throwA[ClientProtocolException]
    }
  }

  "When we call a Post request with a valid link executeHttpPost" should {
    "return a header" in {
      Http4sHttpClient.executeHttpPost("https://duckduckgo.com", Map("Alabama" -> "1", "Iowa" -> "2")).header != null
    }
  }

  "When we call a Post request with a vlid link executeHttpPost" should {
    "return an empty body" in {
      Http4sHttpClient.executeHttpPost("https://duckduckgo.com", Map("Alabama" -> "1", "Iowa" -> "2")).body == ""
    }
  }
  */
}
