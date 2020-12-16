import React, { useState, useEffect } from 'react';
import { 
  useLocation
} from 'react-router-dom';
import { ChrominoSocket, envWebSocketHost, urlNick, urlGameName, urlPlayerCount } from './ChrominoSocket';
import { GameState, Rotation, Position } from './Domain';
import GameBoard from './GameBoard';

function Game() {
  const location = useLocation();
  const [socketActive, setSocketActive] = useState<boolean>(false);
  const [socketError, setSocketError] = useState<string|null>(null);
  const [initialized, setInitialized] = useState<boolean>(false);

  const [gameState, setGameState] = useState<GameState|null>(null);
  const [activeChrominoIndex, setActiveChrominoIndex] = useState<number|null>(null);
  const [chrominoP, setChrominoP] = useState<Position>({ x: 0, y: 0});
  const [chrominoR, setChrominoR] = useState<Rotation>({ N: {}});

  const socket = new ChrominoSocket({
    setSocketActive,
    setSocketError,
    host: envWebSocketHost(),
    
    gameState,
    setGameState
  })

  const gameName = urlGameName(location)
  const selfNick = urlNick(location)
  const playerCount = urlPlayerCount(location)
  useEffect(() => {
    if (!initialized) {
      socket.start(gameName, selfNick, playerCount);
      setInitialized(true);
    }
  });

  return (
    <div className="app">
      <div className="app-header">
        <h4>
          Chromino [{gameName}] :: {!socketActive ? "broken connection" : gameState?.waitingPlayers ? "waiting for players" : (gameState?.winnerIndex != null ? `'${gameState?.players[gameState?.winnerIndex].nick}' won this game` : "game is in session")}
        </h4>
      </div>
      <div className="app-wrapper with-header">
        <div className="app-content with-header with-stretch">
          {!socketActive && "broken connection"}
          {socketActive && gameState?.waitingPlayers && "waiting for more players"}
          {socketActive && gameState && !gameState.waitingPlayers && (
            <GameBoard
              chrominoP={chrominoP}
              setChrominoP={setChrominoP}
              chrominoR={chrominoR}
              setChrominoR={setChrominoR}
              activeChrominoIndex={activeChrominoIndex}
              setActiveChrominoIndex={setActiveChrominoIndex}
              gameState={gameState as GameState}
            ></GameBoard>
          )}
        </div>
        <div className="app-sidebar with-header">
          <h4>Info</h4>
          <div>
            <h6>Connection</h6>
            <p>Status: {socketActive ? "Open" : "Closed"}</p>
            <p>Error: {socketError ? socketError : "None"}</p>
          </div>
          <hr></hr>
          <div>
            <h6>Players [{gameState?.players?.length || "?"}/{gameState?.expectedPlayerCount || "?"}]</h6>
            {(gameState?.players || []).map((player, index) =>
              <p key={index}>[{index + 1}]: {player.nick}{player.nick == selfNick ? "(self)" : ""}</p>
            )}
          </div>
          <hr></hr>
          <div>
            <h6>Chrominos in hand</h6>
            {(gameState?.requesterChrominos || []).map((chromino, index) =>
              <p key={index}>[{index + 1}]: {Object.keys(chromino)[0]}{index == activeChrominoIndex ? "(active)" : ""}</p>
            )}
            {(gameState?.requesterChrominos || []).length == 0 && <p>You have no chrominos at hand</p>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Game
