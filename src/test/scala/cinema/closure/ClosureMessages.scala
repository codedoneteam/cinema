package cinema.closure

object ClosureMessages {
  sealed trait ClosureMessage
  case class InMessage() extends ClosureMessage
  case class OutMessage(i: Int) extends ClosureMessage
}