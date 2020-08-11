package cinema.config

import cinema.config.Config.$
import shapeless.ops.maps.FromMap
import shapeless.ops.record.ToMap
import shapeless.{Default, HList, LabelledGeneric}

import scala.reflect.runtime.universe.TypeTag

trait ConfigAware {
  def config[A <: Product, B, Defaults <: HList, K <: Symbol, V, ARecord <: HList](f: A => B)
                                                                                  (implicit default: Default.AsRecord.Aux[A, Defaults],
                                                                                   toMap: ToMap.Aux[Defaults, K, V],
                                                                                   gen: LabelledGeneric.Aux[A, ARecord],
                                                                                   fromMap: FromMap[ARecord],
                                                                                   configBox: BoxedConfig,
                                                                                   typeTag: TypeTag[A]): B = f($[A]())

  def config[A <: Product, B, Defaults <: HList, K <: Symbol, V, ARecord <: HList](path: String)(f: A => B)
                                                                                  (implicit default: Default.AsRecord.Aux[A, Defaults],
                                                                                   toMap: ToMap.Aux[Defaults, K, V],
                                                                                   gen: LabelledGeneric.Aux[A, ARecord],
                                                                                   fromMap: FromMap[ARecord],
                                                                                   configBox: BoxedConfig,
                                                                                   typeTag: TypeTag[A]): B = f($[A](path))
}