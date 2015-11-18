package controllers

import com.typesafe.config.ConfigFactory
import play.api._
import play.api.libs.json._
import play.api.mvc._
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import scala.collection.JavaConversions._
import services._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index(""))
  }

  def searchUsers(q: String) = Action {
//    Json.obj(
//      "users" -> Json.arr(
//        Json.obj(
//          "name" -> "bob",
//          "age" -> 31,
//          "email" -> "bob@gmail.com"
//        ),
//        Json.obj(
//          "name" -> "kiki",
//          "age" -> 25,
//          "email" -> JsNull
//        )
//      )
//    )

    val users = Twitter.getUsers(q)

    val json = JsArray(users map {user =>
      Json.obj("screenName" -> JsString(user.getScreenName))
    })
    Ok(json)
  }

}

object Twitter {
  def getTweets = {
    val twitter = TwitterFactory.getSingleton()
    val query = new Query("source:twitter4j yusukey")
    val result = twitter.search(query)
    result.getTweets()
  }

  def getUsers(query: String) = {
    val twitter = (new TwitterFactory(Config.twitter)).getInstance()
    twitter.searchUsers(query, 0)
  }
}
