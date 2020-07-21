package cinema.hvector

import scala.language.postfixOps
import scala.reflect.runtime.universe.{Type, TypeTag}

object HVector {

  sealed trait HVector {
    def ::[A](a: A)(implicit typeTag: TypeTag[A]): HVector
    def ++(vector: HVector): HVector
  }

  case class HCons[H, Tail <: HVector](head: (H, Type), tail: Tail) extends HVector  {
    override def ::[A](a: A)(implicit typeTag: TypeTag[A]): HCons[A, HCons[H, Tail]] = HCons((a, typeTag.tpe), this)

    override def toString: String = s"${head._1}[${head._2}]" + "::" + tail.toString

    override def ++(vector: HVector): HVector = {
      if (tail == HNil) HCons(head, vector) else HCons(head, tail ++ vector)
    }
  }

  case object HNil extends HVector {
    override def ::[A](a: A)(implicit typeTag: TypeTag[A]): HCons[A, HNil.type] = HCons((a, typeTag.tpe), this)

    override def ++(vector: HVector): HVector = vector

    override def toString: String = "HNil"
  }

  def apply[A](a: A)(implicit typeTag: TypeTag[A]): HCons[A, HNil.type] = {
    HCons((a, typeTag.tpe), HNil)
  }

  def select[A](hList: HVector)(implicit typeTag: TypeTag[A]): Option[A] = hList match {
    case HCons((head, headType), _) if matchIt(headType)(typeTag.tpe) => Some(head.asInstanceOf[A])
    case hCons: HCons[_, _] => select[A](hCons.tail)
    case _ => None
  }


  private def matchIt[A](headType: Type)(targetType: Type): Boolean = {
    compareType(headType, targetType) && targetType.typeArgs.zip(headType.typeArgs).forall(typePair => matchIt(typePair._1)(typePair._2))
  }

  private def compareType(types: (Type , Type)): Boolean = types._1 =:= types._2
}
