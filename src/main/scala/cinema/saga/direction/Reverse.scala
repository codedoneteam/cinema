package cinema.saga.direction

import akka.actor.typed.DispatcherSelector
import cinema.transaction.AbstractTransaction

case class Reverse[Out](tx: AbstractTransaction[_, Out], ds: DispatcherSelector)