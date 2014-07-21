package com.james.twitter

import akka.actor.Actor
import akka.actor.ActorRef
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future
import scala.util.Failure
import scala.util.Success
import twitter4j._

class DataCollectionActor extends Actor{
  var totalTweets      = 0
  var tweetsWithEmojis = 0
  var tweetsWithURLs   = 0
  var tweetsWithPhoto  = 0

  var allEmojis = Map[String, Int]()
  var allURLs = Map[String, Int]()
  var allHashtags = Map[String, Int]()

  val beginningTime    = System.currentTimeMillis / 1000
  def currentTime      = System.currentTimeMillis / 1000

  def tweetsPerSecond = (totalTweets / (currentTime - beginningTime)).toString
  def tweetsPerMinute = (totalTweets / ((currentTime - beginningTime) / 60.0)).toInt.toString
  def tweetsPerHour   = (totalTweets / ((currentTime - beginningTime) / 60.0 / 60.0)).toInt.toString

  def receive = {
    case ProcessedTweet(emojis, hashtags, urls) => {
      updateCounters(urls.contained, urls.photo, emojis.contained)
      updateAllOccurances(emojis.all, hashtags.all, urls.all)
    }
    case Output => {
      future("Total Tweets: " ++ totalTweets.toString ++ "\n" ++ 
             "Average Tweets (second): " ++ tweetsPerSecond ++ "\n" ++ 
             "Average Tweets (minute): " ++ tweetsPerMinute ++ "\n" ++ 
             "Average Tweets (hour)  : " ++ tweetsPerHour ++ "\n\n" ++ 
             "Percent Tweets with Emojis: " ++ ((tweetsWithEmojis / totalTweets.toDouble) * 100).toInt.toString ++ "%\n" ++
             "Percent Tweets with URLs  : " ++ ((tweetsWithURLs / totalTweets.toDouble) * 100).toInt.toString ++ "%\n" ++ 
             "Percent Tweets with Photos: " ++ ((tweetsWithPhoto / totalTweets.toDouble) * 100).toInt.toString ++ "%\n\n" ++
             "Top URLs    : " ++ findTopThree(allURLs) ++ "\n" ++ 
             "Top Emojis  : " ++ findTopThree(allEmojis) ++ "\n" ++
             "Top Hashtags: " ++ findTopThree(allHashtags) ++ "\n\n\n\n\n\n\n\n").onComplete{
        case Success(succ) => print(succ)
        case Failure(error) => print(error)
      }
    }
  }

  def updateCounters(containedURL: Boolean, containedPhoto: Boolean, containedEmoji: Boolean): Unit = {
    totalTweets      = totalTweets + 1
    tweetsWithEmojis = tweetsWithEmojis + (if(containedEmoji) 1 else 0)
    tweetsWithURLs   = tweetsWithURLs + (if(containedURL) 1 else 0)
    tweetsWithPhoto  = tweetsWithPhoto + (if(containedPhoto) 1 else 0)
  }

  def updateAllOccurances(emojis: List[String], hashtags: List[String], urls: List[String]): Unit = {
    emojis.foreach(emoji => allEmojis = allEmojis + (emoji -> ((allEmojis get emoji).getOrElse(0) + 1)))
    hashtags.foreach(tag => allHashtags = allHashtags + (tag -> ((allHashtags get tag).getOrElse(0) + 1)))
    urls.foreach(url => allURLs = allURLs + (url -> ((allURLs get url).getOrElse(0) + 1))) 
  }

  def findTopThree(mp: Map[String, Int]): String = mp.toList.sortBy{- _._2}.take(3).map(_._1).foldLeft("")(_ ++ " " ++ _) 
}