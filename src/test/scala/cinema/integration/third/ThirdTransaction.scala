package cinema.integration.third

import cinema.config.Config
import cinema.integration.config.Data
import cinema.integration.messages.SecondThird.{SecondThirdMessage, Third}
import cinema.log.Log
import cinema.saga.context.SagaContext
import cinema.transaction.stateful.StatefulTransaction

import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.concurrent.{ExecutionContext, Future}

object ThirdTransaction extends StatefulTransaction[SecondThirdMessage, ThirdState, Boolean] {
  override def apply(stateOpt: Option[ThirdState])(implicit sc: SagaContext[SecondThirdMessage]): Apply = execute { timers: Timers  => _ =>log: Log => {
        case Third(i) if i > 0 =>
          val max = 10
          val state = stateOpt.getOrElse(ThirdState())
          state.counter match {
            case v: Int if v == max =>
              log.info(v) {
                commit(true)
              }
            case _ =>
              timers.startSingleTimer(Third(i), FiniteDuration(1, SECONDS))
              val inc = Config[Data]().inc
              apply(Some(state.copy(state.counter + inc)))
          }
        case Third(0) =>
          Thread sleep 5000
          commit(true)
        case Third(-1) =>
          rollback(false)
      }
    }

  override def unapply(state: Option[ThirdState])(implicit sc: SagaContext[Boolean]): UnApply = compensate { _ => _ => log: Log => { _ =>
      implicit val ec: ExecutionContext = executionContext
      commitFuture {
        Future {
          Third(-1)
        }
      }
  }

  }
}