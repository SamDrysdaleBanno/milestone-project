package com.banno.Entities

object Classes {
  case class Result(name: String, description: String)
  case class searchInfo(searchTerm: String, listOfResults: List[Result])
  case class neededInfoCollection(username: String, allPastSearch: List[String])
  case class UpdatePasswordInfo(username: String, oldPassword: String, newPassword: String)
  case class UserCred(username: String, password: String)
  case class dbUser(username: String, password: String, searchTerms: List[String])
}
