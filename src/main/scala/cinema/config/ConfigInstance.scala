package cinema.config

import cinema.exception.ConfigException
import com.typesafe.config.{Config => TypeConfig}
import shapeless.ops.maps.FromMap
import shapeless.ops.record.ToMap
import shapeless.record._
import shapeless.syntax.std.maps._
import shapeless.{Default, HList, LabelledGeneric}

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe.TypeTag

class ConfigInstance[A <: Product] extends NormalizeAware {

  def apply[D <: HList, K <: Symbol, V, ARecord <: HList]()(implicit default: Default.AsRecord.Aux[A, D],
                                                            toMap: ToMap.Aux[D, K, V],
                                                            gen: LabelledGeneric.Aux[A, ARecord],
                                                            fromMap: FromMap[ARecord],
                                                            configBox: BoxedConfig,
                                                            typeTag: TypeTag[A]): A = {
    val path = normalizePath(typeTag.tpe.baseClasses.head.asClass.name.toString)
    parse(configBox.config, path)
  }

  def apply[Defaults <: HList, K <: Symbol, V, ARecord <: HList](path: String)
                                                                (implicit default: Default.AsRecord.Aux[A, Defaults],
                                                                 toMap: ToMap.Aux[Defaults, K, V],
                                                                 gen: LabelledGeneric.Aux[A, ARecord],
                                                                 fromMap: FromMap[ARecord],
                                                                 configBox: BoxedConfig,
                                                                 typeTag: TypeTag[A]): A = {
    parse(configBox.config, path)
  }

  private def parse[D <: HList, K <: Symbol, V, ARecord <: HList](config: TypeConfig, path: String)(implicit default: Default.AsRecord.Aux[A, D],
                                                                                        toMap: ToMap.Aux[D, K, V],
                                                                                        gen: LabelledGeneric.Aux[A, ARecord],
                                                                                        fromMap: FromMap[ARecord]): A = {
    val map: Map[String, Any] = if (config.hasPath(path)) {
      config.getConfig(path)
        .entrySet()
        .asScala
        .map { x => normalizeKey(x.getKey) -> x.getValue.unwrapped() }
        .toMap
    } else { Map.empty }

    instance(map) match {
      case Some(v) => v
      case _ => throw new ConfigException(map)
    }
  }

  private def instance[D <: HList, K <: Symbol, V, ARecord <: HList](m: Map[String, Any])(implicit
                                                                  default: Default.AsRecord.Aux[A, D],
                                                                  toMap: ToMap.Aux[D, K, V],
                                                                  gen: LabelledGeneric.Aux[A, ARecord],
                                                                  fromMap: FromMap[ARecord]): Option[A] = {
    val defaults: Map[Symbol, Any] = default().toMap[K, V].map { case (k, v) => k -> v }
    val mWithSymbolKeys: Map[Symbol, Any] = m.map { case (k, v) => Symbol(k) -> v }
    (defaults ++ mWithSymbolKeys).toRecord[ARecord].map(LabelledGeneric[A].from)
  }
}