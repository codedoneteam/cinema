package cinema.ref.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors.receive

import scala.concurrent.Promise
import scala.util.Try

object ReplayActor {

  sealed trait ReplayMessage
  case class Ping(promise: Promise[Boolean]) extends ReplayMessage

  def apply(): Behavior[ReplayMessage] = receive[ReplayMessage]((ctx, message) => {
    message match {
      case Ping(promise) =>
        promise.complete(Try(true))
        apply()
    }
  })
}