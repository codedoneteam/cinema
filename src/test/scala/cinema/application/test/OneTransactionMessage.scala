package cinema.application.test

object OneTransactionMessage {
  sealed trait OneTransactionMessage
  case class Process(i: Int) extends OneTransactionMessage
}
