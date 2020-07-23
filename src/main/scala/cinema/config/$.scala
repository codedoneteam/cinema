package cinema.config

object $ {
  def apply[A <: Product] = new ConfigInstance[A]
}