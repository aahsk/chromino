import React, { useState, useEffect } from 'react';
import { 
  useLocation
} from 'react-router-dom';
import { ChrominoSocket, envWebSocketHost, urlNick, urlGameName, urlPlayerCount } from './ChrominoSocket';

function Game() {
  const [socketActive, setSocketActive] = useState<boolean>(false);
  const [socketError, setSocketError] = useState<string|null>(null);
  const location = useLocation();
  useEffect(() => {
    const socket = new ChrominoSocket({
      setSocketActive,
      setSocketError,
      host: envWebSocketHost(),
    })
    socket.stop();
    socket.start(urlGameName(location), urlNick(location), urlPlayerCount(location));
  });
  return (
    <div className="app">
      <div className="app-header">
        <h4>
          Chromino
        </h4>
      </div>
      <div className="app-wrapper">
        <div className="app-content">
          
        </div>
        <div className="app-sidebar">
          <h4>Info</h4>
          <span>
          {socketActive ? "Connection open" : "Connection closed"}
          </span>
          <span>
          {socketError ? `Connection error: ${socketError}` : "Connection has no errors"}
          </span>
        </div>
      </div>
    </div>
  );
}

export default Game
