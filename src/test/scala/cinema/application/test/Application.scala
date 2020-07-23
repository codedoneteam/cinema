package cinema.application.test

import cinema.app.CinemaApp
import cinema.application.test.OneTransactionMessage.Process
import cinema.saga.builder.SagaBuilder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Application extends CinemaApp {

    val future = SagaBuilder()
      .transaction(OneTransaction)
      .build()
      .duration(10 seconds)
      .run(Process)

     val result = Await.result(future, 10 seconds)
     assert(result == "TEST")
}
