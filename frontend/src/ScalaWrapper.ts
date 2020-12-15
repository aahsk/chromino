import { Frontend } from './scala/protocol-fastopt';
import { Rotation } from './Domain';

export interface ScalaWrapperInterface {
    toScala: (data: any) => any | null;
    parseMessage: (data: any) => any | null;

    N: Rotation
    W: Rotation
    S: Rotation
    E: Rotation
    rotateClockwise: (rotation: Rotation) => Rotation
    rotateAntiClockwise: (rotation: Rotation) => Rotation
}
export const ScalaWrapper: ScalaWrapperInterface = (Frontend as any)(JSON.parse, JSON.stringify)
