package cinema.retry.suspend


object RetryMessage {
  sealed trait RetryMessage
  case class InMessage() extends RetryMessage
  case class CalcMessage(i: Int) extends RetryMessage
  case class SecondInMessage() extends RetryMessage
}