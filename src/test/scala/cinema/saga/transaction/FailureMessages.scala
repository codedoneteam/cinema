package cinema.saga.transaction

object FailureMessages {
  sealed trait FailureMessages
  case class InMessage(i: Int) extends FailureMessages
  case class OutMessage(i: Int) extends FailureMessages
}