# Chromino
End-of-bootcamp MVP Scala project. Browser-base, multi-player [Chromino](https://en.wikipedia.org/wiki/Chromino)
server/client.

# Quick start
```
sbt test
sbt compile
sbt fastOptJS
./scala-transfer.sh

# These should run in parallel
sbt boot/run &
cd ./frontend; npm run start &
```

# Tech stack
- WebSocket [(see game route)](src/main/scala/com.aahsk.chromino.http/GameRoute.scala)
- Cats Effect 2 (sprinkled literally everywhere) 
- http4s [(see game route)](src/main/scala/com.aahsk.chromino.http/GameRoute.scala)
- In-memory storage (Cats Ref) [(see game route storage)](./src/main/scala/com.aahsk.chromino.http/GameRoute.scala)
- React w/ TypeScript [(see frontend)](./frontend)
- Scala.js & Circe [(see protocol)](src/main/scala/com.aahsk.chromino.protocol)

# Usage
- `./scala-compile.sh` - Compiles Scala & Scala.JS code    
- `./scala-tidy.sh` - Runs `scalafix` and `scalafmt` 
- `./scala-transfer.sh` - Copies compiled Scala.JS code into `./frontend/src/scala` 
- `sbt boot/run` - Starts a server on http://localhost:9000/ (not configurable for MVP)  
- `cd ./frontend; npm run start` - Starts a development server on http://localhost:3000/ (not configurable for MVP)  

# To do list
A checked box means the feature has been at least started.   

- [x] Game, user, board & chromino piece models [(see domain)](src/main/scala/com/aahsk/chromino/domain)
- [x] Model protocol commands planned in http4s server [(see protocol)](src/main/scala/com.aahsk.chromino.protocol)
- [x] Game model logic / mechanics [(see game controller in logic)](src/main/scala/com.aahsk.chromino.logic/GameController.scala)
- [x] Realize user input validations
- [X] (bonus) Allow concurrent games [(see game route)](src/main/scala/com.aahsk.chromino.http/GameRoute.scala)
- [X] (bonus) Game client (React) [(see frontend)](./frontend)
- [ ] ~~(bonus) Database storage instead of in-memory~~
- [ ] ~~(bonus) Timer for when a player's time to move expires~~

# Notes from author
- It seems that `scalatest` is incapable of testing with `ModuleKind.ESModule` enabled, comment it
    in [build.sbt](./build.sbt) when testing  
- It also seems that the `%%%` operator used in library definitions only works in some certain scopes, which I can't
    reproduce in [project/Dependencies](./project/Dependencies), therefore JS libraries are explicitly defined
    in [build.sbt](./build.sbt)
- Compiling protocol Circe JSON codecs uses up a lot of stack memory, if you're using IntelliJ then replace
    `-Xss1m` with `-Xss2048m` or whatever is appropriate for your case
    in `File => Settings => Build, Execution, Deployment => Compiler => Scala Compiler => Scala Compile Server`,
    also note that the max stack memory is reliant on IDEs max allocated memory, which you can modify under
    `Help => Change Memory Settings`. In addition the IDE can throw `scalac: Error while emitting Codecs.scala; null`
    when trying to run the project, there is no known fix yet. See
    [src/main/scala/com.aahsk.chromino.protocol/Codecs.scala](./src/main/scala/com.aahsk.chromino.protocol/Codecs.scala)
- The in-memory state shared between multiple web-socket connections is mutable and effectful. It feels quite imperative
    using it with Cats Ref looks odd. Haven't yet figured out how to make it prettier
