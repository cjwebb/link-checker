package com.github.cjwebb.linkchecker.actors

import akka.actor.{ActorSystem, ActorRef, ActorPath, Actor}
import scala.xml.parsing.NoBindingFactoryAdapter
import java.net.{URI, URL}
import scala.xml.{XML, Node}
import com.github.cjwebb.linkchecker.MasterWorkerProtocol._
import com.github.cjwebb.linkchecker.actors.LinkCheckerProtocol._
import akka.pattern._
import akka.io.IO
import scala.concurrent.ExecutionContext.Implicits.global

import spray.can.Http
import spray.http._
import HttpMethods._
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._

class Worker(masterPath: ActorPath) extends Actor {

  val master = context.actorSelection(masterPath)
  override def preStart() = master ! WorkerCreated(self)

  override def receive = {
    case WorkIsReady => master ! WorkerRequestsWork(self)
    case WorkToBeDone(work) => doWork(sender, work)
  }

  def doWork(sender: ActorRef, work: CheckLink) = {

    def makeAbsolute(str: String): Option[String] = {
      val uri = new URI(str)
      val checkedURI = new URI(work.url)
      if (uri.isAbsolute) {
        if (uri.getHost == checkedURI.getHost) Some(str) else None
      }
      else {
        Some(s"${checkedURI.getScheme}://${checkedURI.getHost}$str")
      }
    }

    implicit val actorSystem = context.system
    implicit val timeout = Timeout(5 seconds)

    val url = new URL(work.url)
    val results = for {
      r <- HTML.getHTML(url)
    } yield {
      val a = r.map(makeAbsolute).flatten.map(CheckLink(_))
      WorkIsDone(self, LinksChecked(work.url, a))
    }

    results.onFailure {
      case f => println(f.getMessage)
    }

    results pipeToSelection(master)
  }

}

object HTML {

  private def load(content: String): Node = {
    val loader = XML.withSAXParser(new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl().newSAXParser())
    loader.loadString(content)
  }

  def getHTML(site: URL)(implicit actorSystem: ActorSystem, timeout: Timeout): Future[Seq[String]] = {
    (IO(Http) ? HttpRequest(GET, Uri(site.toString))).mapTo[HttpResponse]
      .map(r => r.entity) map { response =>
      val content = HTML.load(response.asString)
      val nodes = (content \\ "a")
        .flatMap(_.attribute("href"))
        .flatten
      nodes map (_.toString)
    }
  }
}
