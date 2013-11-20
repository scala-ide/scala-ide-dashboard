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

  private val AccessTokenParam = s"access_token=$OAuthToken"

  def fetchAllProjects(sender: ActorRef) {
    val f = Future.traverse(Project.allProjects) { p =>
      WS.url(BaseGitHubURL + RepoAPI + p.githubRepo + PullRequestCommand + AccessTokenParam).get().map { response =>
        import model.GitHubJsonReaders._

        val pullRequestsa = (response.json).validate[List[PullRequest]]
        val prs = pullRequestsa.recover {
          case a =>
            println(a)
            Nil
        }.get
        p.copy(pullRequests = prs)
      }
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