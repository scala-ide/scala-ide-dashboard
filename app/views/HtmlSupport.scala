package views

import model.Project

object HtmlSupport {

  def cssIdFor(project: Project): String = {
    project.githubRepo.map {
      _ match {
        case '/' => '_'
        case '.' => '-'
        case c => c
      }
    }
  }

}