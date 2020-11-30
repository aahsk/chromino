package com.aahsk.chromino.domain

case class BoardChromino(chromino: Chromino, centerPosition: Position, centerRotation: Rotation)

case class Board(bag: List[Chromino], pieces: List[BoardChromino])
