import React, { useState } from "react";
import "./GameForm.css";

export interface GameFormData {
  gameName: string
  nick: string
  playerCount: number
}

export interface GameFormConfig {
  onGameSubmit: (gameForm: GameFormData) => void
}

function GameForm(props: GameFormConfig) {
  const [gameName, setGameName] = useState("");
  const [nick, setNick] = useState("");
  const [playerCount, setPlayerCount] = useState(2);
  
  const handleSubmit = (evt: any) => {
      evt.preventDefault();
      console.log('game form submitted')
      props.onGameSubmit({
        gameName,
        nick,
        playerCount
      })
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      <div className="form-field">
        <label className="form-text">
          <span>Game name:</span>
          <input
            type="text"
            value={gameName}
            onChange={e => setGameName(e.target.value)}
          />
        </label>
      </div>
      <div className="form-field">
        <label className="form-text">
          <span>Nickname:</span>
          <input
            type="text"
            value={nick}
            onChange={e => setNick(e.target.value)}
          />
        </label>
      </div>
      <div className="form-field">
        <label className="form-text">
          <span>Player count:</span>
          <input
            type="text"
            value={playerCount}
            onChange={e => setPlayerCount(Number(e.target.value)||0)}
          />
        </label>
      </div>
      <input type="submit" value="Submit" className="form-submit" />
    </form>
  );
}

export default GameForm