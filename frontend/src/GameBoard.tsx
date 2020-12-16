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
  gameState: GameState
}

function GameBoard(props: GameBoardProps) {
  const width = 20;
  const height = 20;
  
  const computeClassName = (s: ChrominoSquare, type: string): string => {
    return `square square-${type} color-${ScalaWrapper.toColorChar(s.color).toLowerCase()}`
  }

  const computeStyle = (s: ChrominoSquare, isStatic: boolean): Record<string, any> => {
    const offsetX = isStatic ? 0 : props.chrominoP.x
    const offsetY = isStatic ? 0 : props.chrominoP.y
    
    return {
      left: (s.position.x + offsetX) * width,
      top: (s.position.y + offsetY) * width,
      width: width,
      height: height,
    }
  }

  const computeSquare = (s: ChrominoSquare, type: string, isStatic: boolean = false, key: string = "") => {
    return (
      <div
        key={key}
        className={computeClassName(s, type)}
        style={computeStyle(s, isStatic)}
      ></div>
    )
  }

  // var x = 0
  // var y = 1
  // var x = 0
  // var y = 1
  // var width = 20

  const handlePress = (evt: KeyboardEvent<HTMLDivElement>) => {
    console.log(evt)
    if (evt.key == "ArrowUp") props.setChrominoP({
      x: props.chrominoP.x,
      y: props.chrominoP.y - 1
    })
    if (evt.key == "ArrowDown") props.setChrominoP({
      x: props.chrominoP.x,
      y: props.chrominoP.y + 1
    })
    if (evt.key == "ArrowLeft") props.setChrominoP({
      x: props.chrominoP.x - 1,
      y: props.chrominoP.y
    })
    if (evt.key == "ArrowRight") props.setChrominoP({
      x: props.chrominoP.x + 1,
      y: props.chrominoP.y
    })
  }
  
  return (
    <div className="game-board-wrap" onKeyDown={handlePress} tabIndex={0}>
      <div className="game-board">
        {props.gameState.board.pieces.map((boardChromino, indexP) => {
          return (ScalaWrapper.toSquares(ScalaWrapper.toScala(boardChromino)) || []).map((square, indexS) => {
            console.log(square)
            return computeSquare(square, "placed", false, `${indexP}.${indexS}`)
          })
        })}
        {/* {(computeSquare(ScalaWrapper.toSquares(boardChromino)) || []).map((square, index) =>
          computeSquare(square, false)
        )} */}
        {computeSquare({ color: null, position: { x: 0, y: 0} }, "center-x", false)}
        {computeSquare({ color: null, position: { x: 0, y: 0} }, "center-y", false)}
        {props.activeChrominoIndex &&
          props.gameState.requesterChrominos[props.activeChrominoIndex] &&
          (ScalaWrapper.toSquares(ScalaWrapper.toScala(props.gameState.requesterChrominos[props.activeChrominoIndex])) || [])
            .map((square, indexS) => {
              return computeSquare(square, "placed", false, `${indexS}`)
            })}
      </div>
    </div>
  );
}

export default GameBoard
