package com.banno.apiStuff

import java.util.concurrent.Executors

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

import cats.effect.IO

import com.banno.Entities.Classes._
import com.banno.services.UserCRUD
import com.banno.milestone.Parser._

import scala.concurrent.ExecutionContext.Implicits.global

object AllRoutes {

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = org.http4s.circe.jsonOf[IO, A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = org.http4s.circe.jsonEncoderOf[IO, A]

  val userService = new UserCRUD("users", "searchs")

  val service = HttpService[IO] {

  // ping check to make sure service is up and working
    case GET -> Root / "ping" =>
      Ok("Pong".asJson)

  // get list of all dbUsers
    case GET -> Root / "users" =>
      Ok(userService.getAll)

  // get most_common_search of all users
    case GET -> Root / "most_common_search" =>
      Ok(userService.allMostFrequentSearch)

  // most_common_search for user sent in req
    case req @ POST -> Root / "most_common_search" => {
      val cred = req.as[UserCred].unsafeRunSync

      if (userService.verifyUser(UserCred(cred.username, cred.password)))
        Ok(individualMostFrequentSearch(userService.get(cred.username).get.searchTerms))
      else
        MethodNotAllowed()
    }

  // get all unique search terms
    case GET -> Root / "search_terms" =>
      Ok(userService.getAllSearchs.getOrElse(List()).distinct)

  // all unique search terms for user sent in req
    case req @ POST -> Root / "search_terms" => {
      val cred = req.as[UserCred].unsafeRunSync

      if (userService.verifyUser(UserCred(cred.username, cred.password)))
        Ok(userService.get(cred.username).get.searchTerms)
      else
        MethodNotAllowed()
    }

  // create a new user
    case req @ POST -> Root / "create_user" => {
      val cred = req.as[UserCred].unsafeRunSync

      val response = userService.create(UserCred(cred.username, cred.password))

      response match {
        case None => MethodNotAllowed()
        case _    => Ok(response.get.asJson)
      }
    }

  // perform a search for user in req and add to their search term list
    case req @ POST -> Root / "search" / term => {
      val cred = req.as[UserCred].unsafeRunSync

      val response = userService.search(UserCred(cred.username, cred.password), term)

      response match {
        case None => MethodNotAllowed()
        case _    => Ok(response.get)
      }
    }

  // change password for user sent in req
    case req @ PUT -> Root / "change_password" => {
      val updateInfo = req.as[UpdatePasswordInfo].unsafeRunSync

      val response = userService.changePassword(updateInfo.username, updateInfo.oldPassword, updateInfo.newPassword)

      response match {
        case None => MethodNotAllowed()
        case _    => Ok(s"User: ${response.get} has changed their password".asJson)
      }
    }
  }
}
