package cinema.config

object Config {
  object $ {
    def apply[A <: Product] = new ConfigInstance[A]
  }
}