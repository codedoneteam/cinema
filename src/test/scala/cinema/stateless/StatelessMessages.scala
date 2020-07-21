package cinema.stateless

object StatelessMessages {
  sealed trait StatelessMessages
  case class InMessage() extends StatelessMessages
  case class OutMessage() extends StatelessMessages
}