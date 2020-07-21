package cinema.integration.messages

object SecondThird {
  sealed trait SecondThirdMessage
  case class Third(i: Int) extends SecondThirdMessage
}