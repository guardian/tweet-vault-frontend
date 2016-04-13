package services

import twitter4j._
import play.api.Logger

object TweetListener extends TwitterInstance {

  def getUserId(username: String): Long = {
    val user = twitter.showUser(username)
    user.getId
  }

  def buildFilterQuery(users: List[String]): FilterQuery = {
    val userIdArray = users.map(getUserId(_))
    Logger.debug(s"Got userIdArray ${userIdArray}")
    val query = new FilterQuery()
    query.follow(userIdArray:_*)
  }

  def updateStream = {
    val allUsers: List[String] = ElasticSearchClient.getUsers
    Logger.debug(s"Updating stream with users ${allUsers}")
    stream.filter(buildFilterQuery(allUsers))
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status): Unit = {
      Logger.debug(s"Got tweet ${status.getId}")
      ElasticSearchClient.putTweet(status)
    }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
      Logger.debug(s"Got deletion notice for tweet ${statusDeletionNotice.getStatusId}")
      ElasticSearchClient.markAsDeleted(statusDeletionNotice.getStatusId)
    }
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }

}


