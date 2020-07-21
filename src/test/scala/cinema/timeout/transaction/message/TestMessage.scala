package cinema.timeout.transaction.message

object TestMessage {
  sealed trait TestMessage
  case object Send extends TestMessage
  case class UnitMessage() extends TestMessage
}