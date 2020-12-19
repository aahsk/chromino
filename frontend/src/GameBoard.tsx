import React, { KeyboardEvent, useState, useEffect } from 'react';
import { GameState, Position, Rotation, BoardChromino, ChrominoSquare } from './Domain';
import './GameBoard.css';
import { ScalaWrapper } from './ScalaWrapper';

export interface GameBoardProps {
  chrominoP: Position
  setChrominoP: React.Dispatch<React.SetStateAction<Position>>
  chrominoR: Rotation
  setChrominoR: React.Dispatch<React.SetStateAction<Rotation>>
  activeChrominoIndex: number|null
  setActiveChrominoIndex: React.Dispatch<React.SetStateAction<number|null>>

  submitMove: () => void
  skipMove: () => void

  gameState: GameState
}

function GameBoard(props: GameBoardProps) {
  const width = 30;
  const height = 30;
  const gutter = 5;
  
  const computeClassName = (s: ChrominoSquare, type: string): string => {
    return `square ${type} color-${ScalaWrapper.toColorChar(s.color).toLowerCase()}`
  }

  const computeStyle = (s: ChrominoSquare, isStatic: boolean): Record<string, any> => {
    const offsetX = isStatic ? 0 : props.chrominoP.x
    const offsetY = isStatic ? 0 : props.chrominoP.y
    
    return {
      left: (-s.position.x + offsetX) * (width + gutter),
      top: (-s.position.y + offsetY) * (width + gutter),
      width: width,
      height: height,
    }
  }

  const computeSquare = (s: ChrominoSquare, type: string, isStatic: boolean = false, key: string|null = null) => {
    return (
      <div
        key={key}
        className={computeClassName(s, type)}
        style={computeStyle(s, isStatic)}
      ></div>
    )
  }

  const handleKeypress = (key: string) => {
    if (key === "ArrowUp") props.setChrominoP({
      x: props.chrominoP.x,
      y: props.chrominoP.y + 1
    })
    if (key === "ArrowDown") props.setChrominoP({
      x: props.chrominoP.x,
      y: props.chrominoP.y - 1
    })
    if (key === "ArrowLeft") props.setChrominoP({
      x: props.chrominoP.x + 1,
      y: props.chrominoP.y
    })
    if (key === "ArrowRight") props.setChrominoP({
      x: props.chrominoP.x - 1,
      y: props.chrominoP.y
    })
    const prevIndex = Math.max(0, (props.activeChrominoIndex || 0) - 1)
    const nextIndex = Math.min((props.activeChrominoIndex || 0) + 1, Math.max(0, props.gameState.requesterChrominos.length - 1))
    if (key === "q") props.setActiveChrominoIndex(prevIndex)
    if (key === "w") props.setChrominoR(ScalaWrapper.rotateClockwise(ScalaWrapper.toScala(props.chrominoR)))
    if (key === "e") props.setChrominoR(ScalaWrapper.rotateAntiClockwise(ScalaWrapper.toScala(props.chrominoR)))
    if (key === "r") props.setActiveChrominoIndex(nextIndex)
    if (key === "f") props.submitMove()
    if (key === "a") props.skipMove()
  }

  const handlePress = (evt: KeyboardEvent<HTMLDivElement>) => {
    handleKeypress(evt.key)
  }

  const placedSquares: Array<ChrominoSquare> = props.gameState.board.pieces.flatMap((boardChromino) => {
    return (ScalaWrapper.toSquares(ScalaWrapper.toScala(boardChromino)) || [])
  })

  const floatingSquares = (() => {
    if (props.activeChrominoIndex == null) return []
    if (props.gameState.requesterChrominos[props.activeChrominoIndex] == null) return []
    const chromino = props.gameState.requesterChrominos[props.activeChrominoIndex]
    const boardChromino: BoardChromino = { centerPosition: props.chrominoP, centerRotation: props.chrominoR, chromino }
    const squares = ScalaWrapper.toSquares(ScalaWrapper.toScala(boardChromino)) || [];

    return squares
      .map((square) => {
        const blocked = !!placedSquares.find((placed: ChrominoSquare) => {
          return placed.position.x === square.position.x &&
            placed.position.y === square.position.y
        })
        const type = blocked ? "floating blocked" : "floating"
        square.position.x -= props.chrominoP.x
        square.position.y -= props.chrominoP.y
        return [type, square]
      })
  })()

  return (
    <div className="game-board-wrap" onKeyDown={handlePress} tabIndex={0}>
      <div className="controls">
        <div><span className="control up" onClick={() => handleKeypress("ArrowUp")}>[↑] Up</span></div>
        <div><span className="control down" onClick={() => handleKeypress("ArrowDown")}>[↓] Down</span></div>
        <div><span className="control left" onClick={() => handleKeypress("ArrowLeft")}>[←] Left</span></div>
        <div><span className="control right" onClick={() => handleKeypress("ArrowRight")}>[→] Right</span></div>
        <div><span className="control previous" onClick={() => handleKeypress("q")}>[q] Previous chromino</span></div>
        <div><span className="control next" onClick={() => handleKeypress("r")}>[r] Next chromino</span></div>
        <div><span className="control clockwise" onClick={() => handleKeypress("e")}>[e] Rotate clockwise</span></div>
        <div><span className="control anticlockwise" onClick={() => handleKeypress("w")}>[w] Rotate anticlockwise</span></div>
        <div><span className="control skip" onClick={() => handleKeypress("a")}>[a] Skip move</span></div>
        <div><span className="control place" onClick={() => handleKeypress("f")}>[f] Place chromino</span></div>
      </div>
      <div className="game-board">
        {/* Render placed piece squares */}
        {placedSquares.map((square, index) => computeSquare(square, "placed", false, `placed-${index}`) )}

        {/* Render floating piece squares */}
        {floatingSquares.map(([type, square], index) => computeSquare(square as ChrominoSquare, type as string, true, `floating-${index}`))}

        {/* Render floating piece cursor */}
        {computeSquare({ color: null, position: { x: 0, y: 0} }, "center-x", true, "center-x")}
        {computeSquare({ color: null, position: { x: 0, y: 0} }, "center-y", true, "center-y")}
      </div>
    </div>
  );
}

export default GameBoard
