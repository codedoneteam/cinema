package cinema.stateful.transaction

object TestOutMessage {
  sealed trait TestOutMessage
  case object TestOut extends TestOutMessage
}