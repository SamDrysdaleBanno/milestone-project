package com.banno.services

import org.specs2.mutable.Specification

import com.banno.Entities.Classes._
import com.banno.milestone.Parser._

import util.control.Breaks._

import scala.concurrent._
import ExecutionContext.Implicits.global

import java.io._


object UserServiceSpec extends Specification {

  val userService = new UserCRUD("testusers", "testsearchs")

// getAll
  "When trying to get a list of all dbUsers getAll" should {
    "return a list of type dbUser" in {
      userService.getAll.isInstanceOf[List[dbUser]]
    }
  }

  "When trying to get a list of all dbUsers getAll" should {
    "return a list of dbUser" in {
      userService.getAll === List(dbUser("sam", "toughPassword", List("mac")),
                                  dbUser("user1", "password1", List("scala","soccer","scala")),
                                  dbUser("user4", "password4", List("dogs","cats")),
                                  dbUser("test1", "password1", List()))
    }
  }

// get
  "When we try to get a user that does exist get" should {
    "return some dbUser" in {
      userService.get("test1") === Some(dbUser("test1", "password1", List()))
    }
  }

  "When we try to get an invalid user get" should {
    "return None" in {
      userService.get("notValid") === None
    }
  }

// create
  "When we try to create an account with a username that already exists create" should {
    "return None" in {
      userService.create(UserCred("test1", "somePass")) === None
    }
  }

  "When we try to create an account with a username that isn't taken create" should {
    "return the username of the created user" in {
      userService.create(UserCred("newUser", "newPass")) === Some("User: newUser has been created")
    }
  }

// changePassword
  "when trying to change a password of a valid user changePassword" should {
    "return a Future of Some String" in {
      userService.changePassword("user1", "password1", "hey") === Some("user1")
    }
  }

  "when trying to change a password of an invalid user changePassword" should {
    "return a Future of None" in {
      userService.changePassword("notvalid", "incorrect", "stillWrong") === None
    }
  }

// addToSearchTerm
  // uses verifyUser to make sure that it is a valid user before adding it
    // those tests are below
  "When passing in valid UserCred and a term to add addToSearchTerm" should {
    "return a Future of Some String" in {
      userService.addToSearchTerm(UserCred("user1", "password1"), "test") === Some("user1")
    }
  }

  "When passing in an invalid UserCred addToSearchTerm" should {
    "return None" in {
      userService.addToSearchTerm(UserCred("notValid", "incorrectPassword"), "search") === None
    }
  }

// verifyUser
  "When passing in a valid UserCred verifyUser" should {
    "return true" in {
      userService.verifyUser(UserCred("sam", "toughPassword")) === true
    }
  }

  "When passing in a user with a valid username and invalid password verifyUser" should {
    "return false" in {
      userService.verifyUser(UserCred("sam", "invalidPassword")) === false
    }
  }

  "When passing in a user with an invalid username verifyUser" should {
    "return false" in {
      userService.verifyUser(UserCred("invalid", "toughPassword")) === false
    }
  }

  // user findMostFrequent tests
  "when we have a user and we call findMostFrequent it" should {
    "find the most frequent search" in {
      userService.findMostFrequent("user1") === "scala"
    }
  }

  "when we have a user that has a tie for the most frequent search findMostFrequent" should {
    "return dogs" in {
      userService.findMostFrequent("user4") === "dogs"
    }
  }

  "when we have a user that hasn't searched for anything findMostFrequent" should {
    "return null" in {

      // ISSUE
      userService.findMostFrequent("test1") === null
    }
  }

  // displayMostFrequentSearchEver tests
  "when we have a list of users allMostFrequentSearch" should {
    "return some list of string" in {
      userService.cleanUp
      userService.allMostFrequentSearch === Some(List("scala"))
    }
  }
}
