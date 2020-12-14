import * as Domain from './scala/domain-fastopt';
import * as Protocol from './scala/protocol-fastopt';
import * as H from 'history';

console.log(Domain, Protocol)

export interface ChrominoSocketConfig {
    setSocketActive: React.Dispatch<React.SetStateAction<boolean>>
    setSocketError: React.Dispatch<React.SetStateAction<string|null>>
    host: string|null
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
        this.config = socketConfig
    }

    stop() {
        console.log("stopping socket")
        if (!this.socket) return;

        try {
            this.socket.close();
            this.socket = null;
            this.config.setSocketActive(false)
            this.config.setSocketError(null)
        } catch (e) {
            this.config.setSocketError(e.getMessage())
        }
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
        try {
            const url = buildGameUrl(this.config.host, gameName, nick, playerCount)
            this.socket = new WebSocket(url.toString());
            this.config.setSocketActive(true)
            this.config.setSocketError(null)
        } catch (e) {
            this.socket = null
            this.config.setSocketActive(false)
            this.config.setSocketError(e.getMessage())
        }
    }
}