# HTTP commands

## Auth
- `/auth/anonymous/register?username` => `{ secret: String }`  
    Register a user session for a given username with a randomly generated secret password
- `/auth/anonymous/login?username` => `{ success: boolean }`
    Log into a user session with a given username with a previously assigned secret password
- `/auth/local/register?username` => `{ secret: String }`  
    Register a user session for a given username with a randomly generated secret password
- `/auth/local/login?username` => `{ success: boolean }`
    Log into a user session with a given username with a previously assigned secret password

## Lobby
- `/game/list` => `{ id, name, creator, createdAt, startedAt, finishedAt, players, maxPlayerCount }`  
    List games that haven't been started yet (or possibly also ones that you're a part of but you left mid-game)
- `/game/create?name&maxPlayerCount` => `{ id }`  
    Sent by anyone to create a new game w/ max count of players allowed
- `/game/join?id` => `{ success: boolean }`  
    Sent by anyone to join a game
- `/game/leave?id` =>  `{ success: boolean }`
    Sent by anyone to leave a game  
- `/game/start?id` =>  `{ success: boolean }`  
    Sent by creator of game to start the game  

## Game
- `/game/inspect?id` => `{ currentTurnChromino, currentTurnPlayer, board: { pieces } }`  
    Sent by anyone to get current state of game (currentTurnChromino is only shown to currentTurnPlayer)   
- `/game/move?id&boardChromino` => `{ success: boolean }`  
    Sent by currentTurnPlayer to make his move by placing chromino on board (boardChromino)  
