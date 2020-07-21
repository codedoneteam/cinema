package cinema.retry


object OutMessage {
  sealed trait OutMessage
  case class SomeOutMessage() extends OutMessage
}