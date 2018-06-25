package com.banno.services

import scala.concurrent.{ExecutionContext, Future}
import com.banno.Entities.Classes._

import com.banno.milestone.Parser._

class UserCRUD(userDb: String, termDb: String)(implicit val executionContext: ExecutionContext) {

  val dbService = new DbService(userDb, termDb)

  def getAll(): List[dbUser] = {
    dbService.dbCreateListDbUser.getOrElse(List(dbUser("Error", "more error", List())))
  }

  def get(username: String): Option[dbUser] = {
    dbService.dbGet(username)
  }

  def create(user: UserCred): Option[String] = {
    get(user.username) match {
      case Some(u) => None
      case None    =>
        Some(s"User: ${dbService.dbCreate(user.username, user.password).get} has been created")
    }
  }

  def changePassword(username: String, oldPassword: String, newPassword: String): Option[String] = {

    if (oldPassword != newPassword) {
      if (verifyUser(UserCred(username, oldPassword)))
        dbService.dbChangePassword(username, newPassword)
      else
        None
    }
    else None
  }

  def addToSearchTerm(cred: UserCred, term: String): Option[String] = {
    if (verifyUser(cred))
      dbService.dbAddSearchTerm(cred.username, term)
    else
      None
  }

  def search(cred: UserCred, term: String): Option[List[Result]] = {
    try {
      addToSearchTerm(cred, term) match {
        case None => None
        case _    => Some(createResultList(term))
      }
    }
    catch {
      case _ : Throwable => None
    }
  }

  def verifyUser(cred: UserCred): Boolean = {
     dbService.dbVerifyUser(cred.username, cred.password) match {
      case None      => false
      case Some(Nil) => false
      case _              => true
    }
  }

  def getAllSearchs(): Option[List[String]] = {
    dbService.dbGetAllSearchs
  }

  def allMostFrequentSearch(): Option[List[String]] = {
    val listOfSearchs = getAllSearchs.getOrElse(List())

    mostFrequentHelper(listOfSearchs)
  }

  // function that returns the most frequent Search of the User
  def findMostFrequent(username: String): String = {
    val listOfDbUser = dbService.dbCreateListDbUser.getOrElse(List(dbUser("ERROR", "403", List("ERROR"))))
    val userToPass = listOfDbUser.find(_.username == username).get

    def findMostFrequentInner(user: dbUser): String = user match{

      case user if user.searchTerms.isEmpty => null
      case _                                  => {
        val frequentList = user.searchTerms.groupBy(identity).mapValues(_.size).maxBy(_._2)

        frequentList._1
      }
    }

    findMostFrequentInner(userToPass)
  }

  def displayMostFrequentEver(informationList: List[neededInfoCollection]): List[String] = {

    val mostSearchedTermList = allMostFrequentSearch.getOrElse(List("Error when trying to find Results Error Code: 404"))

    mostSearchedTermList
  }

  def cleanUp(): Unit = {
    dbService.dbCleanUp
  }
}
