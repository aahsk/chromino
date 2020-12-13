# Chromino
End-of-bootcamp Scala project. Browser-base, multi-player [Chromino](https://en.wikipedia.org/wiki/Chromino)
server/client.

# Tech stack
- WebSocket
- Cats Effect 2
- http4s
- In-memory storage

# To do list
- [x] Game, user, board & chromino piece models 
- [ ] Model protocol commands planned in http4s server  
- [ ] Game model logic / mechanics
- [ ] Realize user input validations
- [ ] ~~(bonus) Database storage instead of in-memory~~
- [ ] ~~(bonus) Timer for when a player's time to move expires~~
- [X] (bonus) Allow concurrent games
- [ ] (bonus) Game client (React)

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
