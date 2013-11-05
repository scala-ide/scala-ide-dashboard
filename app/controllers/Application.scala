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

object Application extends Controller {
  
  def index = Action.async {
    
    val system = Akka.system()
    
    val projectsActor = system.actorSelection("user/projects")
    
    implicit val timeout = Timeout(5 seconds)
    
    val projects = projectsActor ? ProjectsActor.GetProjects
    
    projects collect {
      case ProjectsActor.InitializationInProgress =>
        Ok(views.html.initializationInProgress())
      case ProjectsActor.Projects(projects) =>
        Ok(views.html.index(projects))
    }
    
  }

}