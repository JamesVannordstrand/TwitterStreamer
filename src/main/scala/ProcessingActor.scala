package com.james.twitter

import akka.actor.Actor
import akka.actor.ActorRef
import twitter4j._

class ProcessingActor(dataActor: ActorRef) extends Actor{
  def receive = {
    case status: Status => dataActor ! ProcessedTweet(extractEmojis(status), extractHashtags(status), extractURLs(status))
  }

  def extractEmojis(status: Status): Emojis = {
    val emojis = "[^\u0000-\uFFFF]".r.findAllIn(status.getText)
    Emojis(!emojis.isEmpty, "[^\u0000-\uFFFF]".r.findAllIn(status.getText).toList)
  }
  

  def extractURLs(status: Status): URLs = {
    val urls = status.getURLEntities
    URLs(urls.length > 0
        , urls.map(url => url.getDisplayURL.contains("pic.twitter") || url.getDisplayURL.contains("instagram")).foldLeft(false)(_ || _)  
        , urls.map(_.getDisplayURL.split('/')(0)).toList)
  }

  def extractHashtags(status: Status): Hashtags = Hashtags(status.getHashtagEntities.map(_.getText).toList)  
}