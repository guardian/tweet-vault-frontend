package services

import com.typesafe.config.ConfigFactory
import java.io.File

import twitter4j.conf.ConfigurationBuilder

object Config {
  val values = ConfigFactory.parseFile(new File("conf/keys.conf"))

  val twitter = new ConfigurationBuilder()
    .setOAuthConsumerKey(values.getString("OAuthConsumerKey"))
    .setOAuthConsumerSecret(values.getString("OAuthConsumerSecret"))
    .setOAuthAccessToken(values.getString("OAuthAccessToken"))
    .setOAuthAccessTokenSecret(values.getString("OAuthAccessSecret"))
    .build()
}
