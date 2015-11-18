package services


import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.QueryBuilders
import org.json4s.native.JsonMethods._
import org.json4s._
import twitter4j.{Status, ResponseList}
import scala.collection.JavaConversions._
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.FilterBuilders._
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder

object ElasticSearchClient {

  implicit val formats = org.json4s.DefaultFormats

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
        .field("acknowledged", false)
        .endObject()
      )
      .execute()
      .actionGet()
  }
}

case class Tweet(text: String, status: String)