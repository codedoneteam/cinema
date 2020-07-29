package cinema.transaction.exception

import scala.reflect.runtime.universe.TypeTag


class NoSuchActorRefException[A](tt: TypeTag[A]) extends RuntimeException(tt.toString)