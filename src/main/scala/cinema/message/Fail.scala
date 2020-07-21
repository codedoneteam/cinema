package cinema.message

case class Fail(e: Throwable) extends Message[Nothing]