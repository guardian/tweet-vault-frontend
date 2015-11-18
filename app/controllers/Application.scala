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
    Ok(views.html.index(ElasticSearchClient.getUsers))
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

  def getUsers = Action {
    Ok(Json.obj("users" -> ElasticSearchClient.getUsers.toString))
  }

  def getUsersHtml = Action {
    val users = ElasticSearchClient.getUsers
    Ok(views.html.users(users))
  }

  def getTweets(q: String) = Action {
    val tweets = ElasticSearchClient.getTweets(q)

    val json = JsArray(tweets map {tweet =>
      Json.obj("text" -> JsString(tweet.text))
    })
    Ok(Json.obj("tweets" -> json))
  }

  def getTweetsHtml(user: String) = Action {
    val tweets = ElasticSearchClient.getTweets(user)
    Ok(views.html.tweets(tweets))
  }

  def addUser(user: String) = Action {
    ElasticSearchClient.addUser(user)
    Ok(Json.obj("users" -> ElasticSearchClient.getUsers.toString))
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
