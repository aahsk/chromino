package com.aahsk.chromino.logic

import com.aahsk.chromino.domain._
import scala.util.Random

object GameLogic {
  def joinPlayer(game: Game, nick: String): Game = {
    // Construct user
    val user = User(nick)

    // Add user to game
    val gameWithUser = if (game.waitingPlayers) {
      game.players.find(game.waitingPlayers && _.nick == nick) match {
        case None    => game.copy(players = game.players :+ user)
        case Some(_) => game
      }
    } else {
      game
    }

    // If appropriate, start game
    val gameStarted = if (
      gameWithUser.waitingPlayers && gameWithUser.players.length == gameWithUser.expectedPlayerCount
    ) {
      GameLogic.startGame(gameWithUser).getOrElse(gameWithUser)
    } else {
      gameWithUser
    }

    gameStarted
  }

  def pickRandomChromino(
    bag: List[Chromino],
    ofType: List[Chromino]
  ): Option[(List[Chromino], Chromino)] = {
    val available = bag.filter(ofType.contains(_))
    val maybeChosen = Random
      .shuffle(available)
      .headOption
    maybeChosen.map(chosen => (bag.filterNot(_ == chosen), chosen))
  }

  def pickRandomChrominos(
    initBag: List[Chromino],
    count: Int,
    ofType: List[Chromino]
  ): (List[Chromino], List[Chromino]) = {
    (1 to count).foldLeft((initBag, List[Chromino]())) { case ((bag, chrominos), _) =>
      pickRandomChromino(bag, ofType) match {
        case Some((newBag, pickedChromino)) => (newBag, chrominos :+ pickedChromino)
        case None                           => (bag, chrominos)
      }
    }
  }

  def pickPlayerInitChrominos(
    initBag: List[Chromino],
    nicks: List[String]
  ): (List[Chromino], Map[String, List[Chromino]]) = {
    nicks.foldLeft((initBag, Map[String, List[Chromino]]())) { case ((bag, nickChrominos), nick) =>
      pickRandomChrominos(bag, 8, Chromino.regulars) match {
        case (newBag, pickedChrominos) =>
          (newBag, nickChrominos.updated(nick, pickedChrominos))
      }
    }
  }

  def startGame(game: Game): Option[Game] =
    for {
      (postInitBag, initChromino) <- pickRandomChromino(game.board.bag, Chromino.wildcards)
      (postPlayerBag, newPlayerChrominos) = pickPlayerInitChrominos(
        postInitBag,
        game.players.map(_.nick)
      )

      initRotation = Random.shuffle(Rotation.values).head
      initBoardChromino = BoardChromino(
        initChromino,
        Position(0, 0),
        initRotation
      )

      newGame = game.copy(
        waitingPlayers = false,
        board = game.board.copy(
          bag = postPlayerBag,
          pieces = List(initBoardChromino)
        ),
        playerChrominos = newPlayerChrominos
      )
    } yield newGame

  def createGame(gameName: String, creatorNick: String, expectedPlayerCount: Int): Game =
    Game(
      name = gameName,
      board = Board.empty(),
      creatorNick = creatorNick,
      players = List(User(creatorNick)),
      activePlayerIndex = 0,
      winnerIndex = None,
      playerChrominos = Map[String, List[Chromino]](),
      expectedPlayerCount = expectedPlayerCount,
      waitingPlayers = true
    )
}
