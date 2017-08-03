package com.github.cjwebb.linkchecker

import akka.actor.{Props, ActorSystem}
import com.github.cjwebb.linkchecker.actors.LinkChecker
import com.github.cjwebb.linkchecker.actors.LinkCheckerProtocol.CheckLink


object Boot extends App {

  //val site = new URL(args(0))
  val site = "http://www.bbc.co.uk"

  val actorSystem = ActorSystem()
  val linkChecker = actorSystem.actorOf(Props[LinkChecker], "link-checker")

  linkChecker ! CheckLink(site)

}
