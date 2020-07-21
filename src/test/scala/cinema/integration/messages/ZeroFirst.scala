package cinema.integration.messages


object ZeroFirst {
  sealed trait ZeroFirstMessage
  case class Start(i: Int) extends ZeroFirstMessage
  case class First(i: Int) extends ZeroFirstMessage
}
