package cinema.timeout.transaction.message

import scala.concurrent.Promise

object PromiseMessage {
  sealed trait PromiseMessage
  case class InPromiseMessage(i: Int, promise: Promise[Boolean]) extends PromiseMessage
  case class OutPromiseMessage(s: String, promise: Promise[Boolean]) extends PromiseMessage
}