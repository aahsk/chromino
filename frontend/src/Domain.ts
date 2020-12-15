export interface User {
    nick: String
}

export interface Chromino extends Record<string, any> {}

export interface Position {
    x: number
    y: number
}

export interface Rotation extends Record<string, any> {}

export interface Piece {
    centerPosition: Position
    centerRotation: Rotation
    chromino: Chromino
}

export interface Board {
    pieces: Array<Piece>
}

export interface GameState {
    activePlayerIndex: number
    board: Board
    creatorNick: string
    expectedPlayerCount: number
    name: string
    players: Array<User>
    requesterChrominos: Array<Chromino>
    waitingPlayers: boolean
    winnerIndex: number|null
}