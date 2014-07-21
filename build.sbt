scalaVersion := "2.10.2"
 
libraryDependencies ++= {
  val akkaV = "2.1.4"
  Seq(
    "io.spray"            %   "spray-json_2.10"   % "1.2.5",
    "com.typesafe.akka"   %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"      % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"       % "2.3.9" % "test",
    "org.twitter4j" % "twitter4j-stream" % "3.0.5"
  )
}
