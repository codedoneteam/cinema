package cinema.transaction.exception

import java.util.UUID

class CompensateTimeoutException(id: UUID) extends ConsistencyException(id)