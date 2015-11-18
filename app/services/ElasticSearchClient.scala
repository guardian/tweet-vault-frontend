package services

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

  def getTweets(user: String): List[String] = {
    val searchRequest = client.prepareSearch(indexName).setTypes("tweet")
    val mqp = QueryBuilders.matchQuery("username", user)
    searchRequest.setQuery(mqp)
    searchRequest.execute().actionGet().getHits.map(
      hit => {
        val json = parse(hit.getSourceAsString)
        (json \\ "text").extract[String]
      }
    ).toList
  }

}