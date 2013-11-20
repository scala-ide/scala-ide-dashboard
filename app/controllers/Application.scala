package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import scala.concurrent.Future
import scala.concurrent.duration._
import model.PullRequest
import model.Project
import play.Play
import play.libs.Akka
import akka.pattern.ask
import org.scalaide.dashboard.projects.ProjectsActor
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsValue
import org.scalaide.dashboard.users.UsersActor
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Iteratee
import org.scalaide.dashboard.users.UserActor

object Application extends Controller {

  def index = Action { request =>
    Ok(views.html.index())
  }

  def ws = WebSocket.async[JsValue] { request =>
    val system = Akka.system()

    val usersActor = system.actorSelection("user/users")

    implicit val timeout = Timeout(5.seconds)

    val (out, channel) = Concurrent.broadcast[JsValue]
    val newUser = usersActor ? UsersActor.NewUser(channel)

    newUser collect {
      case UsersActor.User(a) =>
        val in = Iteratee.foreach[JsValue] {
          msg =>
            a ! UserActor.FromClient(msg)
        }
        (in, out)
    }

  }

}