package model

case class Issue(number: Int, url: String, labels: Seq[Label], comments: Int)
case class Label(name: String, color: String)