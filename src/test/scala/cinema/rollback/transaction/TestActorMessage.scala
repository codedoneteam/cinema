package cinema.rollback.transaction

object TestActorMessage {
  sealed trait TestActorMessage
  case object Send extends TestActorMessage
}