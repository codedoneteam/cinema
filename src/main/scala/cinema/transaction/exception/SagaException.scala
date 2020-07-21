package cinema.transaction.exception

class SagaException[A](val message: A) extends RuntimeException(message.toString)