package computerdatabase

import scala.collection.mutable
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RockPaperScissorsSimulation extends Simulation {

  val scn = scenario("rock-paper-scissors concurrent users scenario")
    //    .feed(usersFeeder)
    .exec(ws("RockPaperScissors").connect("ws://localhost:8080/rock-paper-scissors"))
    .pause(1 seconds)
    //    .exec(websocket("Chat").sendMessage("""{"state" : "Hello, I'm ${user}", "userInput": "NONE"}"""))
    .exec(ws("RockPaperScissors").sendText("""{"state" : "START", "userInput": "NONE"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "ROCK"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "ROCK"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "ROCK"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "SCISSORS"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "SCISSORS"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "SCISSORS"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "IN_PROGRESS", "userInput": "SCISSORS"}"""))
    .pause(1 seconds)
    .exec(ws("RockPaperScissors").sendText("""{"state" : "END", "userInput": "NONE"}"""))
    .pause(1 seconds)
    .exec(ws("Chat").close)

  setUp(scn.inject(constantConcurrentUsers(2000) during (15 seconds)))
}