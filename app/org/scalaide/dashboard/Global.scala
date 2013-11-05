package org.scalaide.dashboard

import play.api.Application
import play.api.GlobalSettings
import akka.actor.ActorSystem
import play.libs.Akka
import org.scalaide.dashboard.projects.ProjectsActor
import org.scalaide.dashboard.projects.DataProcessorActor

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    initializeActors()
  }
  
  private def initializeActors() {
    val system = Akka.system()
    system.actorOf(DataProcessorActor.props, "dataprocessor")
    system.actorOf(ProjectsActor.props, "projects")
  }

}