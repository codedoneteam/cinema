package cinema.saga.direction

import akka.actor.typed.DispatcherSelector
import cinema.transaction.AbstractTransaction

case class Forward[In](tx: AbstractTransaction[In, _], ds: DispatcherSelector)