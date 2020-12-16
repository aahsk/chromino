import * as H from 'history';
import { GameState, Position, Rotation, BoardChromino } from './Domain';
import { ScalaWrapper } from './ScalaWrapper';

export interface ChrominoSocketConfig {
    setSocketActive: React.Dispatch<React.SetStateAction<boolean>>
    setSocketError: React.Dispatch<React.SetStateAction<string|null>>
    host: string|null

    gameState: GameState|null
    setGameState: React.Dispatch<React.SetStateAction<GameState|null>>

    chrominoP: Position
    chrominoR: Rotation

    activeChrominoIndex: number|null
    setActiveChrominoIndex: React.Dispatch<React.SetStateAction<number|null>>
}

export const envWebSocketHost = (): string | null => {
    return process.env.REACT_APP_BACKEND_WEBSOCKET_HOST || null
}

export const urlGameName = (location: H.Location<H.LocationState>): string | null => {
    return new URLSearchParams(location.search).get("gameName")?.toString() || null
}

export const urlNick = (location: H.Location<H.LocationState>): string | null => {
    return new URLSearchParams(location.search).get("nick")?.toString() || null
}

export const urlPlayerCount = (location: H.Location<H.LocationState>): number | null => {
    return Number(new URLSearchParams(location.search).get("playerCount")?.toString()) || null
}

export const buildGameUrl = (host: string, gameName: string, nick: string, playerCount: number) => {
    const path = `/game/${gameName}`
    let params: any = {
        nick: nick
    }
    if (!!playerCount) params['expectedPlayerCount'] = playerCount
    
    return new URL(`${path}?${new URLSearchParams(params).toString()}`, host)
}

export class ChrominoSocket {
    config: ChrominoSocketConfig
    socket: WebSocket | null = null
    constructor(socketConfig: ChrominoSocketConfig) {
        console.log("init")
        this.config = socketConfig
        this.submitMove.bind(this)
    }

    stop(errorMessage: string = "") {
        console.log(`stopping socket w/ reason: ${errorMessage}`)
        if (!this.socket) return;

        this.socket.close();
        this.socket = null;
        this.config.setSocketActive(false)
        this.config.setSocketError(errorMessage)
    }

    start(gameName: string | null, nick: string | null, playerCount: number | null) {
        console.log("starting socket")
        if (!!this.socket) return;

        // Validate input parameters
        this.socket = null
        this.config.setSocketActive(false)
        if (!this.config.host) { this.config.setSocketError("Chromino host not set"); return }
        if (!gameName) { this.config.setSocketError("Game name not set"); return }
        if (!nick) { this.config.setSocketError("Nick not set"); return }
        if (!playerCount) { this.config.setSocketError("Player count not set"); return }

        // Init socket
        const url = buildGameUrl(this.config.host, gameName, nick, playerCount)
        this.socket = new WebSocket(url.toString())
        this.config.setSocketActive(true)
        this.config.setSocketError(null)
        
        // Init error handler
        this.socket.onerror = (evt: Event): any => {
            console.log("websocket encountered error", evt)
            this.stop("Websocket encountered error")
        }

        // Init message handler
        this.socket.onmessage = (evt: Event): any => {
            const serialized = ("data" in evt) ? evt["data"] : "";
            const json = ScalaWrapper.parseMessage(serialized)
            if (!json) {
                this.config.setSocketError("Received invalid message"); return
            }

            const command = ("command" in json) ? json["command"] : null;
            const payload = ("payload" in json) ? json["payload"] : null;

            this.processCommand(command, payload)
        }
    }

    resetActiveChrominoIndex(newState: GameState|null) {
        const availableChrominos = newState?.requesterChrominos.length || this.config.gameState?.requesterChrominos.length || 0
        const activeIndex = this.config.activeChrominoIndex

        // Turn off active index if there are no chrominos
        if (availableChrominos === 0) {
            this.config.setActiveChrominoIndex(null)
            return
        }

        // Set activeIndex if its not set and there are chrominos
        if (activeIndex === null) {
            if (availableChrominos > 0) {
                this.config.setActiveChrominoIndex(0)
            }
            return
        }

        // Bump down activeIndex if its on 
        if (activeIndex > (availableChrominos - 1)) {
            this.config.setActiveChrominoIndex((availableChrominos - 1))
            return
        }
    }

    submitMove() {
        if (!this.socket) return;
        if (this.config.activeChrominoIndex == null) return;
        const chromino = this.config.gameState?.requesterChrominos[this.config.activeChrominoIndex]
        if (!chromino) return;

        const boardChromino: BoardChromino = {
            centerPosition: this.config.chrominoP,
            centerRotation: this.config.chrominoR,
            chromino
        }

        const message = {
            command: "submitMove",
            payload: {
                boardChromino
            }
        }

        console.log("submit", message)
        this.socket.send(JSON.stringify(message))
    }

    processCommand(command: string, payload: any) {
        switch (command) {
            case ("playerJoined"):
                this.playerJoined(payload)
                break;
            case ("gameStateMessage"):
                this.gameStateMessage(payload)
                break;
            case ("connectionMigrated"):
                this.connectionMigrated(payload)
                break;
            default:
                console.log(`received unknown message ${command}`)
                break;
        }
    }

    playerJoined(payload: any) {
        if (this.config.gameState == null) {
            console.log("players joined, but game isn't defined")
            return
        } else {
            console.log("players joined")
        }

        const players = ("players" in payload) ? payload["players"] : [];
        this.config.setGameState({
            ...this.config.gameState,
            players
        })
    }

    gameStateMessage(payload: any) {
        console.log("game state changed")
        const state = ("state" in payload) ? payload["state"] : [];
        this.resetActiveChrominoIndex(state)
        this.config.setGameState(state)
    }

    connectionMigrated(payload: any) {
        this.stop("This nickname has a new connection")
    }
}