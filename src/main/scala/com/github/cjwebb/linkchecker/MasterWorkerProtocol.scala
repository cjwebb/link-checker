package com.github.cjwebb.linkchecker

import akka.actor.ActorRef
import com.github.cjwebb.linkchecker.actors.LinkCheckerProtocol._

object MasterWorkerProtocol {
  // Messages from Workers
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef, result: LinksChecked)

  // Messages to Workers
  case class WorkToBeDone(work: CheckLink)
  case object WorkIsReady
  case object NoWorkToBeDone
}
