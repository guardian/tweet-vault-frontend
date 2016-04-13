import play.api._
import services.{TweetListener, Config}

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Application startup...")

    TweetListener.stream.addListener(TweetListener.simpleStatusListener)
    TweetListener.updateStream
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
}
