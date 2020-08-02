package cinema.exception

import scala.reflect.runtime.universe.TypeTag


class SagaBuilderException[A](typeTag: TypeTag[A]) extends RuntimeException(s"Transaction with type ${typeTag.tpe} already present in saga")
