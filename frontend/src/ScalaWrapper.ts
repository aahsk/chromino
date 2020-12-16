import { Frontend } from './scala/protocol-fastopt';
import { Rotation, BoardChromino, ChrominoSquare, ChrominoColor } from './Domain';

export interface ScalaWrapperInterface {
    // Scala <-> TypeScript
    toScala: (data: any) => any | null;
    parseMessage: (data: any) => any | null;

    // Rotation
    N: Rotation
    W: Rotation
    S: Rotation
    E: Rotation

    R: ChrominoColor
    B: ChrominoColor
    P: ChrominoColor
    Y: ChrominoColor
    G: ChrominoColor
    X: ChrominoColor

    // Chromino color
    rotateClockwise: (rotation: Rotation) => Rotation
    rotateAntiClockwise: (rotation: Rotation) => Rotation

    // Chromino
    toSquares: (boardChromino: BoardChromino) => Array<ChrominoSquare>

    // Custom
    isLike: (a: any, b: any) => boolean
    toColorChar: (color: ChrominoColor|null) => string
}

export const ScalaWrapper: ScalaWrapperInterface = (wrapper => {
    // Custom
    wrapper.isLike = function(a: any, b: any): boolean {
        return JSON.stringify(a) == JSON.stringify(b)
    }

    wrapper.toColorChar = function(color: ChrominoColor|null): string {
        if (wrapper.isLike(color, wrapper.R)) return "R"
        if (wrapper.isLike(color, wrapper.B)) return "B"
        if (wrapper.isLike(color, wrapper.P)) return "P"
        if (wrapper.isLike(color, wrapper.Y)) return "Y"
        if (wrapper.isLike(color, wrapper.G)) return "G"
        if (wrapper.isLike(color, wrapper.X)) return "X"
        return "U"
    }

    return wrapper
})((Frontend as any)(JSON.parse, JSON.stringify))
