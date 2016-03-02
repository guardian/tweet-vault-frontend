package services


import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.QueryBuilders
import org.json4s.native.JsonMethods._
import org.json4s._
import twitter4j.TwitterFactory
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.conf.ConfigurationBuilder
import scala.collection.JavaConversions._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.FilterBuilders._
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder
import org.elasticsearch.search.sort.SortOrder
import play.api._

object ElasticSearchClient {

  implicit val formats = org.json4s.DefaultFormats

  val tf: TwitterFactory = new TwitterFactory(Config.twitter)
  val twitter = tf.getInstance

  val indexName = "tweet-vault"

  val client = new TransportClient()
    .addTransportAddress(new InetSocketTransportAddress(Config.values.getString("ElasticSearch.Address"), Config.values.getString("ElasticSearch.Port").toInt))

  def getUsers: List[String] = {
    client.prepareSearch(indexName).setTypes("user").execute().actionGet().getHits.flatMap(
      hit => {
        val json = parse(hit.getSourceAsString)
        (json \\ "usernames").extract[List[String]]
      }
    ).toList
  }

  def getTweets(user: String): List[Tweet] = {
    val searchRequest = client.prepareSearch(indexName).setTypes("tweet")
    val mqp = QueryBuilders.matchQuery("username", user)
    searchRequest.setQuery(mqp)
    searchRequest.addSort("created_at", SortOrder.DESC)
    searchRequest.execute().actionGet().getHits.map(
      hit => {
        val json = parse(hit.getSourceAsString)
        val text = (json \\ "text").extract[String]
        val status = (json \\ "status").extract[String]
        Tweet(text, status)
      }
    ).toList
  }

  def addUser(username: String) = {
    val response = client.prepareIndex(indexName, "user", username)
      .setSource(jsonBuilder()
        .startObject()
        .field("name", username)
        .startArray("usernames").value(username).endArray()
        .endObject()
      )
      .execute()
      .actionGet()

    ElasticSearchClient.putTimeLine(twitter.getUserTimeline(username))
  }

  def putTimeLine(tweets: ResponseList[Status]) = {
    for (tweet <- tweets) {
      putTweet(tweet)
    }
  }

  def putTweet(tweet: Status): Unit = {
    val document = XContentFactory.jsonBuilder()
      .startObject()
      .field("status", "live")
      .field("username", tweet.getUser.getScreenName)
      .field("name", tweet.getUser.getName)
      .field("created_at", tweet.getCreatedAt)
      .field("favorite_count", tweet.getFavoriteCount)
      .field("retweet_count", tweet.getRetweetCount)
      .field("text", tweet.getText)
      .field("in_reply_to", tweet.getInReplyToScreenName)
      .startArray("media")
    for (attachment <- tweet.getMediaEntities) {
      document.startObject()
      document.field("id", attachment.getId)
      document.field("url", attachment.getMediaURL)
      document.field("type", attachment.getType)
      document.endObject()
    }
    document.endArray()
      .startArray("mentioned")
    for (mentionedUser <- tweet.getUserMentionEntities) {
      document.startObject()
      document.field("id", mentionedUser.getId)
      document.field("username", mentionedUser.getScreenName)
      document.field("name", mentionedUser.getName)
      document.endObject()
    }
    document.endArray()
    document.field("", tweet.getMediaEntities)
      .endObject()
    val updateRequest = new UpdateRequest(indexName, "tweet", tweet.getId.toString).doc(document)
    updateRequest.docAsUpsert(true)
    client.update(updateRequest).get()
  }
}

case class Tweet(text: String, status: String)