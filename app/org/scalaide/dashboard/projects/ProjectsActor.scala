package org.scalaide.dashboard.projects

import akka.actor.Actor
import akka.actor.Props
import model.Project
import akka.actor.ActorRef
import akka.event.LoggingReceive
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorIdentity
import akka.actor.Identify
import akka.actor.ActorIdentity

class ProjectsActor extends Actor {
  
  import ProjectsActor._
  
  var projects: List[Project] = Nil
  var dataProcessor: ActorRef = null
  
  def receive = initialization
  
  val initialization: Receive = LoggingReceive {
    case Initialize =>
      context.actorSelection("/user/dataprocessor") ! Identify(SomeId)
    case ActorIdentity(SomeId, Some(dpa)) =>
      dataProcessor = dpa
      dataProcessor ! DataProcessorActor.FetchAllProjects
    case Projects(ps) =>
      projects = ps
      context.become(processing)
      context.system.scheduler.scheduleOnce(60 second, dataProcessor, DataProcessorActor.FetchAllProjects)
    case GetProjects =>
      sender ! InitializationInProgress
  }
  
  def processing: Receive = LoggingReceive {
    case Projects(ps) =>
      projects = ps
      context.system.scheduler.scheduleOnce(60 second, dataProcessor, DataProcessorActor.FetchAllProjects)
    case GetProjects =>
      sender ! Projects(projects)
  }
  
  override def preStart() {
    self ! Initialize
  }
  

}

object ProjectsActor {
  def props = Props[ProjectsActor]
  
  val SomeId = "id-1"
  
  // messages
  private case object Initialize
  case object InitializationInProgress
  case object GetProjects
  // may need a different message for projects coming from DataProcessor, with some acces control 
  case class Projects(projects: List[Project])
}