package com.aahsk.chromino.logic

import com.aahsk.chromino.domain._
import scala.util.Random

object GameLogic {
  def validateBoardChromino(
    pieces: List[BoardChromino],
    addedPiece: BoardChromino
  ): Either[String, BoardChromino] = {
    val existingSquares = pieces.flatMap(_.squared())
    val addedSquares    = addedPiece.squared()

    // Validate no overlap
    val isOverlapping = !addedSquares
      .forall(addedSquare =>
        existingSquares.forall(existingSquare =>
          !(addedSquare.position.x == existingSquare.position.x &&
            addedSquare.position.y == existingSquare.position.y)
        )
      )
    val maybeNoOverlapSquares = if (isOverlapping) {
      Left("Chromino overlaps existing board chrominos")
    } else {
      Right(addedSquares)
    }

    // Map every added square with its immediate neighbours
    val maybeWithNeighbours: Either[String, List[(ChrominoSquare, List[ChrominoSquare])]] =
      maybeNoOverlapSquares.flatMap(squares => {
        val withNeighbours = squares.map(added =>
          (
            added,
            existingSquares.filter(existing => {
              val up =
                existing.position.y == added.position.y + 1 && existing.position.x == added.position.x
              val down =
                existing.position.y == added.position.y - 1 && existing.position.x == added.position.x
              val left =
                existing.position.y == added.position.y && existing.position.x == added.position.x - 1
              val right =
                existing.position.y == added.position.y && existing.position.x == added.position.x + 1

              up || down || left || right
            })
          )
        )

        val allNeighbours = withNeighbours.flatMap { case (_, neighbours) => neighbours }
        if (allNeighbours.isEmpty) {
          Left("No neighbouring chrominos present")
        } else if (allNeighbours.size < 2) {
          Left("At least two neighbouring chrominos required")
        } else {
          Right(withNeighbours)
        }
      })

    // Validate 2/3 squares touch only equal colors
    maybeWithNeighbours
      .flatMap(squares => {
        val isColorMatching = squares.forall { case (added, neighbours) =>
          neighbours.forall(neighbouring => {
            val identical = neighbouring.color == added.color
            val eitherWildcard = neighbouring.color == ChrominoColor.X ||
              added.color == ChrominoColor.X

            identical || eitherWildcard
          })
        }
        if (isColorMatching) {
          Right(addedPiece)
        } else {
          Left("Chromino colors don't match colors of nearby chrominos")
        }
      })
  }

  def submitMove(
    game: Game,
    submitNick: String,
    submitBoardChromino: BoardChromino
  ): Either[String, Game] =
    for {
      playerWithChrominos <- game.playerChrominos
        .find { case (nick, _) =>
          nick == submitNick
        }
        .toRight(s"Player '${submitNick}' is not present in game")
      (nick, playerChrominos) = playerWithChrominos
      activeUser <- game.players
        .lift(game.activePlayerIndex)
        .toRight("Game has no active player which can make moves")
      _ <-
        if (activeUser.nick == nick) {
          Right(())
        } else {
          Left(s"Player '${activeUser.nick}' is moving")
        }
      _ <-
        if (game.waitingPlayers) {
          Left("Game not started yet ")
        } else {
          Right(())
        }
      _ <-
        if (game.winnerIndex.isDefined) {
          Left("Game already finished")
        } else {
          Right(())
        }
      playerChromino <- playerChrominos
        .find(_ == submitBoardChromino.chromino)
        .toRight("Chromino isn't available")
      submitRotation = submitBoardChromino.centerRotation
      submitPosition = submitBoardChromino.centerPosition
      validBoardChromino <- validateBoardChromino(
        game.board.pieces,
        BoardChromino(playerChromino, submitPosition, submitRotation)
      )
      newPlayerIndex = (game.activePlayerIndex + 1) % game.players.size
      newPlayerChrominos = game.playerChrominos.map {
        case (playerChrominosNick, chrominos) if playerChrominosNick == nick =>
          (playerChrominosNick, chrominos.filterNot(_ == validBoardChromino.chromino))
        case playerChrominos => playerChrominos
      }
      newPieces = game.board.pieces :+ validBoardChromino
      newBoard  = game.board.copy(pieces = newPieces)
      newGame = game.copy(
        board = newBoard,
        activePlayerIndex = newPlayerIndex,
        playerChrominos = newPlayerChrominos
      )
    } yield newGame

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
