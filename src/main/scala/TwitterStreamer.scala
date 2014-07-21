package com.james.twitter

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import twitter4j._

object StatusStreamer {

  def main(args: Array[String]) {
    val system = ActorSystem("ProcessingSystem")
    val dataActor = system.actorOf(Props(new DataCollectionActor))
    val processingActors = system.actorOf(Props(new ProcessingActor(dataActor)).withRouter(RoundRobinRouter(4)))
    val twitterStream = new TwitterStreamFactory(Util.config).getInstance
    twitterStream.addListener(Util.simpleStatusListener(processingActors))
    twitterStream.sample
    system.scheduler.schedule(5 seconds, 5 seconds, dataActor, Output)
  }
}

object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(configuration.consumerKey)
    .setOAuthConsumerSecret(configuration.consumerSecret)
    .setOAuthAccessToken(configuration.accessToken)
    .setOAuthAccessTokenSecret(configuration.tokenSecret)
    .build

  def simpleStatusListener(processingActors: ActorRef) = new StatusListener() {
    def onStatus(status: Status) {processingActors ! status}
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}