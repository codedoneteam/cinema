package cinema.retry.suspend

object OutMessage {
  sealed trait OutMessage
  case class SomeOutMessage(i: Int) extends OutMessage
}
