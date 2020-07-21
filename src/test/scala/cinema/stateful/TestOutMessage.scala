package cinema.stateful

object TestOutMessage {
  sealed trait TestOutMessage
  case object TestOut extends TestOutMessage
}