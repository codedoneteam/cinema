package cinema.suspend.transaction

object OutMessage {
  sealed trait OutMessage
  case class Two(i: Int) extends OutMessage
}
