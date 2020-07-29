package cinema.stateful.transaction

object TestMessage {
  sealed trait TestTransactionMessage
  case object First extends TestTransactionMessage
  case object Second extends TestTransactionMessage
}