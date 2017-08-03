package com.github.cjwebb.linkchecker.actors

import akka.actor._
import com.github.cjwebb.linkchecker.actors.LinkCheckerProtocol._

class LinkChecker extends Actor {

  val master = context.actorOf(Props[Master])

  // fire up workers
  1 to 10 foreach { _ =>
    context.actorOf(Props(classOf[Worker], master.path))
  }

  override def receive = {
    case link @ CheckLink(url) => master ! link
    case msg => println(msg)
  }
}

object LinkCheckerProtocol {
  case class CheckLink(url: String)
  case class LinksChecked(url: String, results: Seq[CheckLink])
}