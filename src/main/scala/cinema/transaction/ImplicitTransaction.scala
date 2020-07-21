package cinema.transaction

import cinema.message.Payload

trait ImplicitTransaction[In] {
  implicit class PayloadMessage(in: In) extends Payload(in)
}
