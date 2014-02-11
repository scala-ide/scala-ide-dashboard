package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

object GitHubJsonReaders {

  import play.api.libs.json.Reads._

  implicit def pullRequestReads: Reads[PullRequest] = (
    (__ \ "number").read[Int] and
    (__ \ "html_url").read[String] and
    (__ \ "_links" \ "comments" \ "href").read[String]).apply(PullRequest)


  implicit def labelReads: Reads[Label] = (
    (__ \ "name").read[String] and
    (__ \ "color").read[String]) apply (Label)

  implicit def issueReads: Reads[Issue] = (
    (__ \ "number").read[Int] and
    (__ \ "url").read[String] and
    (__ \ "labels").read[Seq[Label]] and
    (__ \ "comments").read[Int]).apply(Issue)

}

object WebAppJsonWriters {

  import play.api.libs.json.Writes._

  implicit def pullRequestWrites = new Writes[PullRequest] {
    def writes(pr: PullRequest): JsValue = {
      Json.obj(
        "number" -> pr.number,
        "url" -> pr.url)
    }
  }
  
  implicit def issueWrites = new Writes[Issue] {
    def writes(i: Issue): JsValue = {
      Json.obj(
        "number" -> i.number,
        "url" -> i.url)
    }
  }

  implicit def projectWrites: Writes[Project] = (
    (__ \ "name").write[String] and
    (__ \ "category").write[String] and
    (__ \ "githubRepo").write[String] and
    (__ \ "pullRequests").lazyWrite(list[PullRequest](pullRequestWrites)) and
    (__ \ "issues").lazyWrite(list[Issue](issueWrites)))(unlift(Project.unapply))
}