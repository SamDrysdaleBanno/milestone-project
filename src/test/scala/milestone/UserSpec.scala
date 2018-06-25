package com.banno.milestone


import org.specs2.mutable.Specification
import com.banno.milestone.Parser._
import com.banno.Entities.Classes._
import com.banno.services.DbService

object UserSpec extends Specification {

  // parser tests
    // createNeededInfoCollection tests
  "when we have a user with searchs createNeededInfoCollection" should {
    "return a neededInfoCollection" in {
      createNeededInfoCollection(dbUser("not-empty", "password", List("search"))).isInstanceOf[neededInfoCollection]
    }
  }

  "when we have a user that hasn't searched for anything createNeededInfoCollection" should {
    "return a neededInfoCollection with the user and empty lists" in {

      createNeededInfoCollection(dbUser("empty", "password", List())) === neededInfoCollection("empty", List())
    }
  }

  // createCollection tests
  "when we have an empty user list createCollection" should {
    "return an empty list" in {
      createCollection(List()) === None
    }
  }
}
