package cinema.transaction.exception

import java.util.UUID

class ConsistencyException(id: UUID) extends RuntimeException("Saga UUID " + id)