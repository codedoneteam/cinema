package cinema.stateful

object TestMessage {
  sealed trait TestTransactionMessage
  case object First extends TestTransactionMessage
  case object Second extends TestTransactionMessage
}