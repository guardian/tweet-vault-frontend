package services

import twitter4j.{TwitterStream, TwitterStreamFactory, Query, TwitterFactory}

trait TwitterInstance {
  val tf: TwitterFactory = new TwitterFactory(Config.twitter)
  val twitter = tf.getInstance
  val stream: TwitterStream = new TwitterStreamFactory(Config.twitter).getInstance()
}

object Twitter extends TwitterInstance {

  def getTweets = {
    val query = new Query("source:twitter4j yusukey")
    val result = twitter.search(query)
    result.getTweets()
  }

  def getUsers(query: String) = {
    twitter.searchUsers(query, 0)
  }

}
