# Build saga

```
val saga = SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction, DispatcherSelector.blocking)
      .transaction(ThirdTransaction)
      .build()
```



# Run saga

```
val future = saga.duration(100 seconds)
                 .run(First(1))
```





# Transactions of saga

## Simple transaction


```
object FirstTransaction extends Transaction[One, Two] {

  override def apply(implicit sc: SagaContext[One]): Apply = execute { log: Log => {
     case _ =>
        // Transaction business logic
        commit(Two)
   }
  }

  override def unapply(implicit sc: SagaContext[Two]): UnApply = compensate { log: Log => {
        case _ =>
            // Compensate logic
            commit(One)
    }
  }
}
```


## Transaction with retries


```
object SomeRetryTransaction extends Transaction[One, Two] {

  val retryStrategy: RetryStrategy = {
    case e: ExpectedTestException => 2 seconds
    case _ => throw new OtherExpectedTestException
  }

  override def apply(implicit sc: SagaContext[One]): Apply = execute(retryStrategy) { log: Log => {
      case _ =>
         // Transaction business logic
         commit(Two)
    }
  }

  override def unapply(implicit sc: SagaContext[Two]): UnApply = compensate(retryStrategy) { log: Log => {
      case _ =>
         // Compensate logic
         commit(One)
     }
    }
   }
}
```




## Suspend transaction


```
object SelectionAwareTransaction extends SuspendTransaction[First, Second] {

  override def apply(implicit sc: SagaContext[First]): Apply = execute { self: Self => log: Log => {
      case First(0) =>
        self ! First(1)
        await
      case First(1) =>
        commit(Second(1))
    }
  }

  override def unapply(implicit sc: SagaContext[Second]): UnApply = compensate { self: Self => log: Log => {
       case _ => // Compensate logic
      }
    }
}
```


## Stateful transaction

```
object SomeStatefulTransaction extends StatefulTransaction[One, SomeState, Two] {

  override def apply(state: Option[SomeState])(implicit sc: SagaContext[One]): Apply = execute {timers: Timers => self: Self => log: Log => {
      case _ => // Transaction business logic
    }
  }

  override def unapply(state: Option[SomeState])(implicit sc: SagaContext[Two]): UnApply = compensate {timers: Timers => self: Self => log: Log => {
      case _ => // Compensate logic
    }
  }
}
```





#Some minor features

## Read config from saga transaction


```
object FirstTransaction extends Transaction[One, Two] {

  case class SomeConfig(i: Int = 0)

  override def apply(implicit sc: SagaContext[One]): Apply = execute { log: Log => {
     case _ => config { someConfig: SomeConfig =>
             // Transaction business logic
     }
   }
  }

  override def unapply(implicit sc: SagaContext[Two]): UnApply = compensate { log: Log => {
        case _ => // Compensate logic
    }
  }
}
```




## Create actor

```
object Application extends CinemaApp {
    actorOf(Mongo()) { ref =>
        // Some logic
    }
}
```


## Select actor from transaction


## Select actor from transaction

```
object SomeTransaction extends Transaction[One, Two] {

  case class SomeConfig(i: Int = 0)

  override def apply(implicit sc: SagaContext[One]): Apply = execute { log: Log => {
     case _ => actorSelection[SomeActor] { actorRef =>
             // Transaction business logic
     }
   }
  }

  override def unapply(implicit sc: SagaContext[Two]): UnApply = skip
}

```





# Build framework
sbt clean assembly release