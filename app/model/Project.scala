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
        Project(pConf.getString("name"), pConf.getString("github_repo"))
    }(collection.breakOut)
    
  }
}

case class Project(name: String, githubRepo: String, val pullRequests: List[PullRequest] = Nil) {
  
}