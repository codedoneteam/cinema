package cinema.suspend.transaction

object InMessage {
  sealed trait InMessage
  case class One(i: Int) extends InMessage
  case class CalcResult(i: Int) extends InMessage
}
