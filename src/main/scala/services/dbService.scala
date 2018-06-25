package com.banno.services

import doobie._
import doobie.implicits._
import cats.effect.IO

import com.banno.Entities.Classes._

case class partialUser(username: String, password: String)

class DbService(userDb: String, termDb: String){

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:milestonedatabase", "sam", ""
  )

  def dbCreateListDbUser: Option[List[dbUser]] = {
    try {
    // create a list of partial users
      val partialList = {
        val sqlString = s"select * from $userDb"
        Query0[partialUser](sqlString, None).stream.compile.toList.transact(xa).unsafeRunSync
      }
    // then loop through each of those partial users
      val finalList: List[dbUser] = createDbUserList(partialList)

      Some(finalList)
    }
    catch {
      case _ : Throwable => None
    }
  }

  def createDbUserList(partUserList: List[partialUser]): List[dbUser] = {

    def recCreateDbUserList(acc: List[dbUser], partUserList: List[partialUser]): List[dbUser] = partUserList match {
      case Nil => acc
      case head :: tail => {
        val userToAdd = dbUser(head.username, head.password, getUsersSearchs(head.username).getOrElse(List("ERROR REC")))
        recCreateDbUserList(acc :+ userToAdd, tail)
      }
    }

    recCreateDbUserList(List(), partUserList)
  }

  def getUsersSearchs(inputUsername: String): Option[List[String]] = {
    try {
      val listToReturn = {
        val sqlString = s"select term from $termDb where username = '$inputUsername'"
        Query0[String](sqlString, None).stream.compile.toList.transact(xa).unsafeRunSync
      }

      Some(listToReturn)
    }
    catch {
      case _ : Throwable => {
        None
      }
    }
  }

  def dbGet(inputUsername: String): Option[dbUser] = {
    try {
      val partUser = {
        val sqlString = s"select * from $userDb where username = '$inputUsername'"
        Query0[partialUser](sqlString, None).stream.take(1).compile.toList.transact(xa).unsafeRunSync.head
      }

      val userSearchs = getUsersSearchs(inputUsername)

      userSearchs match {
        case None => None
        case _ => {
          Some(dbUser(partUser.username, partUser.password, userSearchs.get))
        }
      }
    }
    catch {
      case _ : Throwable => None
    }
  }

  def dbCreate(inputUsername: String, inputPassword: String): Option[String] = {
    try {
      // add to user
      val insert = {
        // issue here is with the userDb param....
        sql"insert into users values ($inputUsername, $inputPassword)"
          .update
          .run
          .transact(xa)
          .unsafeRunSync
      }

      Some(inputUsername)
    }
    catch {
      case _ : Throwable => None
    }
  }

  def dbChangePassword(inputUsername: String, newPassword: String): Option[String] = {
    try {
      val sqlUpdate  = {
        sql"update users set password = $newPassword where username = $inputUsername"
          .update
          .run
          .transact(xa)
          .unsafeRunSync
      }

      Some(inputUsername)
    }
    catch {
      case _ : Throwable => None
    }
  }

  def dbAddSearchTerm(usernameToAdd: String, term: String): Option[String] = {
    try {
      val sqlInsert = {
        sql"insert into searchs values ($usernameToAdd, $term)"
          .update
          .run
          .transact(xa)
          .unsafeRunSync
      }

      Some(usernameToAdd)
    }
    catch {
      case _ : Throwable => None
    }
  }

  def dbVerifyUser(inputUsername: String, inputPassword: String): Option[List[partialUser]] = {
    try {
      val toReturn = {
        val sqlString = s"select * from $userDb where username = '$inputUsername' and password = '$inputPassword'"
        Query0[partialUser](sqlString, None).stream.compile.toList.transact(xa).unsafeRunSync
      }

      toReturn match {
        case Nil => None
        case _ => Some(toReturn)
      }
    }
    catch {
      case _ : Throwable => None
    }
  }

  def dbGetAllSearchs(): Option[List[String]] = {
    try {
      val listToReturn = {
        val sqlString = s"select term from $termDb"
        Query0[String](sqlString, None).stream.compile.toList.transact(xa).unsafeRunSync
      }

      Some(listToReturn)
    }
    catch {
      case _ : Throwable => None
    }
  }

  // used when testing
  def dbCleanUp(): Unit = {
    sql"delete from users where username = 'newUser'"
      .update
      .run
      .transact(xa)
      .unsafeRunSync

    sql"delete from searchs where username = 'newUser'"
      .update
      .run
      .transact(xa)
      .unsafeRunSync

    sql"delete from testsearchs where username = 'sam' and term = 'search'"
      .update
      .run
      .transact(xa)
      .unsafeRunSync

    sql"update users set password = 'password1' where username = 'user1'"
      .update
      .run
      .transact(xa)
      .unsafeRunSync

    sql"delete from searchs where username = 'user1' and term = 'test'"
      .update
      .run
      .transact(xa)
      .unsafeRunSync
  }
}
