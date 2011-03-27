package org.frontierdevelopers.scalaworkshop

import java.lang.Integer

case class Player(name: String, gold: Integer = 0, location: Site) {
  val weapons = Nil

  override def toString = name + ", gold=" + gold + ", location = " + location
}

class Enemy {
}

case class Dragon(location: Location = new Location(0, 0)) {
}

case class Site(name: String, description: String) {
  override def toString = description
}

case class Location(x: Integer, y: Integer) {
  def move(dx: Integer, dy: Integer) = copy(x = x.intValue + dx.intValue, y = y.intValue + dy.intValue)
}

object GameMap {
  val townSquare = Site("Scalata town square", "Town square in Scalata, Scalata, a nice village with elves or equivalent")
  val jail = Site("Scalata jail", "Jail in Scalata.  This place reeks of justice")
  val clearing = Site("Clearing by a pond", "Clearing, near a pond with deer grazing nearby.  No sign of snakes, but there is a foreboding and smelly cave nearby")
  val cave = Site("Cave", "Cavernous cave.  A scorpion runs past you, screaming")
  val links = Map(townSquare -> ( clearing :: jail :: Nil ),
                  clearing -> ( townSquare :: Nil ),
                  jail -> ( townSquare :: Nil ),
                  clearing -> ( cave :: Nil ),
                  cave -> ( Nil )
  )
}

trait Action {
  def update(state: GameState): GameState
}

class ExitAction extends Action {
  def update(state: GameState) = {
    System.exit(0)
    null
  }

  override def toString = "exit game"
}

case class GameState(player: Player = new Player("Dorbax", 0, GameMap.townSquare)) {
  val enemies = new Dragon() :: Nil

  def update(input: Action) = input.update(this)

  def options = {
    val travelLinks = for ( link <- GameMap.links(player.location) ) yield {
      TravelTo(link)
    }
    val systemOptions = new ExitAction :: Nil
    travelLinks ::: systemOptions
  }

  override def toString = {
    player + ", enemies = " + ( for ( e <- enemies if e.location == player.location ) yield {
      e
    } )
  }
}

case class TravelTo(destination: Site) extends Action {
  override def toString = "Travel to " + destination.name

  def update(state: GameState) = state.copy(state.player.copy(location = destination))
}

class ConsoleGame {
  def loopGame(state: GameState): Unit = {
    println(state)
    val options = state.options
    val numberedOptions = for ( i <- 0 until options.length ) yield {
      i + ". " + options(i)
    }
    println(numberedOptions.mkString("\n"))
    val line: String = readLine()
    val choice = Integer.parseInt(line)
    loopGame(state.update(options(choice)))
  }
}

object ConsoleGame {
  def main(args: Array[String]) {
    new ConsoleGame().loopGame(new GameState)
  }
}