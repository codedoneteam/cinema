package cinema.retry.transaction

object RetryMessage {
  sealed trait RetryMessage
  case class InMessage() extends RetryMessage
  case class SecondInMessage() extends RetryMessage
}