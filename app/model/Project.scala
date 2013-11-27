package model

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters
import com.typesafe.config.Config

object Project {

  def allProjects: List[Project] = {

    val conf = ConfigFactory.load("projects.conf");

    import JavaConverters._

    conf.getConfigList("projects").asScala.map {
      pConf: Config =>
        Project(pConf.getString("name"), pConf.getString("category"), pConf.getString("github_repo"))
    }(collection.breakOut)

  }

  def delta(oldProjects: List[Project], newProjects: List[Project]): List[Project] = {
    // assumes that projects are not added or removed, just changed
    newProjects diff oldProjects
  }
}

case class Project(name: String, category: String, githubRepo: String, val pullRequests: List[PullRequest] = Nil) {

}