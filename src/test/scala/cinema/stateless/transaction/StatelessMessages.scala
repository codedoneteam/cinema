package cinema.stateless.transaction

object StatelessMessages {
  sealed trait StatelessMessages
  case class InMessage() extends StatelessMessages
  case class OutMessage() extends StatelessMessages
}