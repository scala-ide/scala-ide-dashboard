package org.scalaide.dashboard

import play.api.Application
import play.api.GlobalSettings
import akka.actor.ActorSystem
import play.libs.Akka
import org.scalaide.dashboard.projects.ProjectsActor
import org.scalaide.dashboard.projects.DataProcessorActor
import org.scalaide.dashboard.users.UsersActor

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    initializeActors()
  }
  
  private def initializeActors() {
    val system = Akka.system()
    val dataProcessor = system.actorOf(DataProcessorActor.props, "dataprocessor")
    val projects = system.actorOf(ProjectsActor.props(dataProcessor), "projects")
    val actors = system.actorOf(UsersActor.props(projects), "users")
  }

}