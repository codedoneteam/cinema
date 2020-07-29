package cinema.retry.transaction

object OutMessage {
  sealed trait OutMessage
  case class SomeOutMessage() extends OutMessage
}