package cinema.application.test

object OneTransactionMessage {
  sealed trait OneTransactionMessage
  case object Process extends OneTransactionMessage
}
