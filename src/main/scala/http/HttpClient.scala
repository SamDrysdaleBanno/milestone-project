package http

import org.http4s.client.blaze._
import cats.effect.IO
import org.http4s.client._

case class HttpResponse(header: String, body: String, statusCode: Int)

trait HttpClient {
  def executeHttpPost(url: String, body: Map[String,String]): HttpResponse
  def executeHttpGet(url: String): HttpResponse
}

object Http4sHttpClient {

  val client: Client[IO] = Http1Client[IO]().unsafeRunSync

  def executeHttpGet(url: String): HttpResponse = {
    val response = client.get(url) {
      case resp => IO.pure(HttpResponse(resp.headers.mkString, resp.as[String].map("" + _).unsafeRunSync, resp.status.code))
    }

    response.unsafeRunSync
  }
/*
  def executeHttpPost(url: String, body: Map[String, String]): Unit = {
    val req = POST(Uri.uri(fromString(url)), UrlForm("q" -> "http4s"))
    val responseBody = Http1Client[IO]().flatMap(_.expect[String](req))
    println(responseBody.unsafeRunSync())
    //responseBody.unsafeRunSync()
  }
  */
}
