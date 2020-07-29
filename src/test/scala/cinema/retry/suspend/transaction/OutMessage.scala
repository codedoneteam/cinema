package cinema.retry.suspend.transaction

object OutMessage {
  sealed trait OutMessage
  case class SomeOutMessage(i: Int) extends OutMessage
}
