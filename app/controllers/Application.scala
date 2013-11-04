package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import scala.concurrent.Future
import model.PullRequest
import model.Project
import play.Play

object Application extends Controller {

  private val OAuthToken = Play.application().configuration().getString("dashboard.oauthtoken")

  private val BaseGitHubURL = "https://api.github.com/"
  private val RepoAPI = "repos/"

  private val repo = "scala-ide/scala-ide"

  private val PullRequestCommand = "/pulls?"

  private val AccessTokenParam = s"access_token=$OAuthToken"

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index = Action.async {

    getData.map { pullRequests =>
      Ok(views.html.index(pullRequests))
    }

  }

  private def getData(): Future[Seq[(Project, Seq[PullRequest])]] = {
    val fs = Project.allProjects.map { p =>
      WS.url(BaseGitHubURL + RepoAPI + p.githubRepo + PullRequestCommand + AccessTokenParam).get().map { response =>
        import model.PullRequestReader._

        val pullRequests = (response.json).validate[Seq[PullRequest]]
        val prs = pullRequests.recover {
          case a =>
            println(a)
            Nil
        }.get
        (p, prs)
      }
    }
    Future.sequence(fs)
  }

}