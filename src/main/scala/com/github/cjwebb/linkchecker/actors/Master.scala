package com.github.cjwebb.linkchecker.actors

import akka.actor._
import com.github.cjwebb.linkchecker.actors.LinkCheckerProtocol._
import com.github.cjwebb.linkchecker.MasterWorkerProtocol._
import scala.collection.mutable.{Map, Queue, Set}

class Master extends Actor {

  val workers = Map.empty[ActorRef, Option[CheckLink]]
  val workQueue = Queue.empty[CheckLink]

  val checkedLinks = Set.empty[CheckLink]

  override def receive = {
    case WorkerCreated(actorRef) =>
      workers += (actorRef -> None)
      notifyWorkers()

    case WorkerRequestsWork(actorRef) =>
      if (!workQueue.isEmpty) actorRef ! WorkToBeDone(workQueue.dequeue())

    case links @ CheckLink(url) =>
      workQueue.enqueue(links)

    case WorkIsDone(actorRef, result) =>
      result.results foreach { r =>
        if (!checkedLinks.contains(r)) {
          checkedLinks += r
          println(r)
          workQueue.enqueue(r)
        }
      }
      notifyWorkers()
  }

  // notify workers of work, if they're not busy
  def notifyWorkers() = {
    if (!workQueue.isEmpty) {
      workers.foreach {
        case (worker, m) if (m.isEmpty) => worker ! WorkIsReady
        case _ =>
      }
    }
  }
}
