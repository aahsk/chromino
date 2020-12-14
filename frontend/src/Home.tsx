import React from 'react';
import GameForm, { GameFormData } from './GameForm';
import { withRouter } from 'react-router-dom';

function Home(props: any) {
  const handleGameSubmit = (gameForm: GameFormData) => {
    console.log('redirecting to game')
    props.history.push({
      pathname: '/game',
      search: new URLSearchParams({
        gameName: gameForm.gameName,
        nick: gameForm.nick,
        playerCount: gameForm.playerCount.toString(),
      }).toString()
    })
  }
  return (
    <div>
      <div className="app-wrapper">
        <div className="app-content">
          <h4>
            Chromino
          </h4>
          <GameForm onGameSubmit={handleGameSubmit}/>
        </div>
      </div>
    </div>
  );
}

export default withRouter(Home)