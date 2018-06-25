package com.banno.milestone

import org.http4s.server.blaze._
import cats.effect.IO

import com.banno.apiStuff.AllRoutes._

object Main extends App {

  val builder = BlazeBuilder[IO].bindHttp(8080).mountService(service, "/").start

  val server = builder.unsafeRunSync

  server.awaitShutdown()

}
