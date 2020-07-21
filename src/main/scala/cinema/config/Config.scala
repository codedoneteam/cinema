package cinema.config

object Config {
  def apply[A <: Product] = new ConfigInstance[A]
}