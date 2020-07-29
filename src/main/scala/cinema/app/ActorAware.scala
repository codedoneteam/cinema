package cinema.app

import java.util.UUID

import akka.actor.typed.DispatcherSelector._
import akka.actor.typed.{ActorRef, Behavior, DispatcherSelector}
import cinema.exception.ActorException
import cinema.manager.CinemaManager.ProduceRef

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Promise}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}

trait ActorAware {
  this: CinemaManagerAware =>
    def actorOf[A, B](behavior: Behavior[A], name: String = UUID.randomUUID().toString, ds: DispatcherSelector = default())
                     (callback: ActorRef[A] => B)
                     (implicit typeTag: TypeTag[A]): Future[B] = {

      val actionPromise = Promise[B]()
      val actorRefPromise = Promise[ActorRef[A]]()
      cinemaManager ! ProduceRef(behavior, name, actorRefPromise, typeTag, ds)
      implicit val ex: ExecutionContextExecutor = ExecutionContext.global
      actorRefPromise.future.onComplete {
        case Success(ref) => actionPromise.complete(Try(callback(ref)))
        case Failure(e) => throw new ActorException
      }
      actionPromise.future
    }
}