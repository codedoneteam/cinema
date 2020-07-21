package cinema.retry

object RetryMessage {
  sealed trait RetryMessage
  case class InMessage() extends RetryMessage
  case class SecondInMessage() extends RetryMessage
}