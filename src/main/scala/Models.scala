package com.james.twitter

case object Output

case class Emoji(
  unified: String
)

case class ProcessedTweet(
  emojis: Emojis,
  hashtags: Hashtags,
  urls: URLs
)

case class Emojis(
  contained: Boolean,
  all: List[String]
)

case class URLs(
  contained: Boolean,
  photo: Boolean,
  all: List[String]
)

case class Hashtags(
  all: List[String]
)