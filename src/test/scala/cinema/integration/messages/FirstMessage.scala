package cinema.integration.messages

object FirstMessage {
  sealed trait FirstSecondMessage
  case class Second(i: Int) extends FirstSecondMessage
}