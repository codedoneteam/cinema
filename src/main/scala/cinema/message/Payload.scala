package cinema.message

case class Payload[+A](data: A) extends Message[A]