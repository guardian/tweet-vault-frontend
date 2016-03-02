package services


case class User(elasticSearchId: String, name: String, userNames: List[String]) {

  override def toString:String = {
    s"${name} with handles ${userNames}"
  }

}
