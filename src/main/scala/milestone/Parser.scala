package com.banno.milestone

import http._

import com.banno.Entities.Classes._

object Parser {

  val errorStatement: String = "Error whenx trying to find Results Error Code: 404"

  def createCollection(listOfUsers: List[dbUser]): Option[List[neededInfoCollection]] = listOfUsers match{
    case Nil                                => None
    case listOfUsers if listOfUsers.isEmpty => Some(List())
    case _                                  => {
      val correctCollection: List[neededInfoCollection] = for {
        user <- listOfUsers
      } yield {
        createNeededInfoCollection(user)
      }
      Some(correctCollection)}
    }


    // loops through all search and makes a list and then groups and counts
    def mostFrequentHelper(searchList: List[String]): Option[List[String]] = searchList match{
      case searchList if searchList.isEmpty => Some(List())
      case _                                          => {
        // creates a list of all searchs ever from all users

        try{

          // then use groupBy and mapValues to find the most frequent search
          val groupedTerms = searchList.groupBy(identity).mapValues(_.size)

          val maxList = findMaxList(groupedTerms)

          Some(maxList)
        } catch {
          case _ : Throwable => None
        }
      }
    }

    def individualMostFrequentSearch(searchList: List[String]): Option[List[String]] = {
      mostFrequentHelper(searchList)
    }

    def findMaxList(mapList: Map[String, Int]): List[String] = {

      def recMaxList(acc: List[String], maxVal: Int, mapList: List[(String, Int)]): List[String] = mapList match {
        case Nil => acc
        case head :: tail if ( head._2 > maxVal ) => recMaxList(List(head._1), head._2, tail)
        case head :: tail if ( head._2 == maxVal ) => recMaxList(acc :+ head._1, maxVal, tail)
        case head :: tail => recMaxList(acc, maxVal, tail)
      }

      recMaxList(List(), 0, mapList.toList)
    }


    def createNeededInfoCollection(user: dbUser): neededInfoCollection = user match{
      case user if user.searchTerms.isEmpty => neededInfoCollection(user.username, user.searchTerms)
      case _                                => {
        neededInfoCollection(user.username, user.searchTerms)
      }
    }

    // Returns a string in url format
    // takes the search term and makes the url
    def generateURL(searchTerm: String): Option[String] = {
      try {
        Some(s"https://api.duckduckgo.com/?q=${searchTerm}&format=json")
      } catch {
        case _ : Throwable => None
      }
    }

    // this takes in a response String
    // uses the defined regular expression to pull out certain information
    // returns the needed information in a list of strings
    def useRegex(responseString: String): Option[List[String]] = {
      try {
        val regex = """((\\">).*?(\."|","))""".r
        Some(regex.findAllIn(responseString.replace("\\u", "")).toList)
      } catch {
        case _ : Throwable => None
      }
    }

    def parseUnparsedResultList(unparsed: List[String]): Option[List[Result]] = {
      /* in here we need to parse the unparsed list and then for each
      * we need to create a Result and then add that to a list to return
      */
      try {
        val listOfFinalResults: List[Result] = for (unparsedString <- unparsed) yield {
          // this is where we will do the individual string parsing
          // function here to do the parsing
          createResult(unparsedString).getOrElse(Result("Error when trying to find Results", "Error Code: 404"))
        }
        Some(listOfFinalResults)
      } catch {
        case _ : Throwable => None
      }
    }

    // this funtion parses the elements of the List useRegex returns
    // returns  a Result
    def createResult(unparsedString: String): Option[Result] = {
      //ex. /">Scala (programming language)</a>A general-purpose programming language providing support for functional programming and a strong...","
      // need |           name           |    |                             description                                                         |

      try {
        val splitList: Array[String] = unparsedString.substring(3, unparsedString.length - 1).split("</a>")

        Some(Result(splitList(0), splitList(1)))
      } catch {
        case _ : Throwable => None
      }
    }

    def createResultList(searchTerm: String): List[Result] = {
      // Result Consists of name: String , description: String

      val url: String = generateURL(searchTerm).getOrElse(errorStatement)

      // Get request to the URL
      val getResponseString: String = Http4sHttpClient.executeHttpGet(url).body

      // use the regex to get a list of strings that will further be parsed
      val unparsedResultList: List[String] = useRegex(getResponseString).getOrElse(List(errorStatement))

      // return List of Results by further parsing
      parseUnparsedResultList(unparsedResultList).getOrElse(List(Result("Error when trying to find Results", "Error Code: 404")))
    }
  }
