package cinema.transaction.types

import akka.actor.typed.scaladsl.TimerScheduler
import akka.actor.typed.{ActorRef, Behavior}
import cinema.message.Message

trait TransactionTypes[In, Out] {
  type Apply = Behavior[Message[In]]

  type UnApply = Behavior[Message[Out]]

  type Timers = TimerScheduler[Message[In]]

  type Self = ActorRef[Message[In]]
}
