package model

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

case class PullRequest(number: Int, url: String, commentsUrl: String)

object PullRequestReader {
  
  implicit def pullRequestReads: Reads[PullRequest] = (
      (__ \ "number").read[Int] and
      (__ \ "html_url").read[String] and
      (__ \ "_links" \ "comments" \ "href").read[String])(PullRequest)
  
}