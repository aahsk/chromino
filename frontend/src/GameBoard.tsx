import React, { useState, useEffect } from 'react';
import { GameState } from './Domain';

export interface GameBoardProps {
  gameState: GameState
}

function GameBoard(props: GameBoardProps) {
  return (
    <div className="game-board">

    </div>
  );
}

export default GameBoard
