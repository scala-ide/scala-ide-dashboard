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

class ProjectsActor(dataProcessor: ActorRef) extends Actor {

  import ProjectsActor._

  var projects: List[Project] = Nil

  var subscribers = Set[ActorRef]()

  def receive = initialization

  val initialization: Receive = LoggingReceive {
    case Initialize =>
      dataProcessor ! DataProcessorActor.FetchAllProjects
    case Projects(ps) =>
      projects = ps
      subscribers.foreach { s =>
        s ! Projects(ps)
      }
      context.become(processing)
      context.system.scheduler.scheduleOnce(60.second, dataProcessor, DataProcessorActor.FetchAllProjects)
    case SubscribeAndGetAll =>
      subscribers += sender
    case Unsubscribe =>
      subscribers -= sender
  }

  val processing: Receive = LoggingReceive {
    case Projects(ps) =>
      val delta = Project.delta(projects, ps)
      projects = ps

      for {
        s <- subscribers
        p <- delta
      } s ! ModifiedProject(p)

      context.system.scheduler.scheduleOnce(60.second, dataProcessor, DataProcessorActor.FetchAllProjects)
    case SubscribeAndGetAll =>
      subscribers += sender
      sender ! Projects(projects)
    case Unsubscribe =>
      subscribers -= sender
  }

  override def preStart() {
    self ! Initialize
  }

}

object ProjectsActor {
  def props(dataProcessor: ActorRef) = Props(classOf[ProjectsActor], dataProcessor)

  val SomeId = "id-1"

  // messages
  private case object Initialize
  case object InitializationInProgress
  // may need a different message for projects coming from DataProcessor, with some access control 
  case class Projects(projects: List[Project])
  case class ModifiedProject(project: Project)
  case object SubscribeAndGetAll
  case object Unsubscribe
}