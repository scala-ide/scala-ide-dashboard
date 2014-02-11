package org.scalaide.dashboard.projects

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import model.Project
import model.PullRequest
import play.Play
import play.api.libs.ws.WS
import akka.event.LoggingReceive

class DataProcessorActor extends Actor {

  import DataProcessorActor._

  def receive = LoggingReceive {
    case FetchAllProjects =>
      fetchAllProjects(sender)
  }
  
}

object DataProcessorActor {

  def props = Props[DataProcessorActor]

  // messages

  case object FetchAllProjects // returns a ProjectsActor.Projects

  // processing

  private val OAuthToken = Play.application().configuration().getString("dashboard.oauthtoken")

  private val BaseGitHubURL = "https://api.github.com/"
  private val RepoAPI = "repos/"

  private val PullRequestCommand = "/pulls?"
  private val IssuesRequestCommand = "/issues?"

  private val AccessTokenParam = s"access_token=$OAuthToken"

  private def fetchProjectPullRequests(p: model.Project): Future[List[model.PullRequest]] = {
    WS.url(BaseGitHubURL + RepoAPI + p.githubRepo + PullRequestCommand + AccessTokenParam).get().map { response =>
      import model.GitHubJsonReaders._
      val pullRequestsa = (response.json).validate[List[PullRequest]]
      pullRequestsa.recover {
        case a =>
          println(a)
          Nil
      }.get
    }
  }
  private def fetchProjectIssues(p: model.Project): Future[List[model.Issue]] = {
    WS.url(BaseGitHubURL + RepoAPI + p.githubRepo + IssuesRequestCommand + AccessTokenParam).get().map { response =>
      import model.GitHubJsonReaders._
      val issuesA = (response.json).validate[List[model.Issue]]
      issuesA.recover {
        case a =>
          println(a)
          Nil
      }.get.filter(_.labels.isEmpty)  // TODO - Filter here or later?
    }
  }
  
  def fetchAllProjects(sender: ActorRef) {
    val f = Future.traverse(Project.allProjects) { p =>
      val pulls = fetchProjectPullRequests(p)
      val issues = fetchProjectIssues(p)
      for {
        ps <- pulls
        is <- issues
      } yield p.copy(pullRequests = ps.sortBy(_.number), issues = is)
    }

    f.map {
      ps =>
        println(s"Sending projects to $sender")
        // handy to do this here, because sender is stable, but it might be easier to understand 
        // if it was done in receive()
        sender ! ProjectsActor.Projects(ps)
    }
  }

}