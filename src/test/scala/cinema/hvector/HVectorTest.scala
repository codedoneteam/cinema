package cinema.hvector

import cinema.hvector.HVector.{HNil, HVector, select}
import cinema.hvector.elements.Container
import cinema.hvector.elements.DoubleElement
import org.scalatest.FlatSpec
import scala.language.postfixOps
import scala.reflect.runtime.universe._


class HVectorTest extends FlatSpec {

  "Apply" should "create HVector" in {
    val list = HVector(42)
    assert(list == 42 :: HNil)
  }

  "Head" should "get from the list" in {
    val list = 42:: false :: Some("OK") :: 42 :: HNil
    val head = list.head
    assert(head == (42, typeOf[Int]))
  }

  "Tail" should "get from the list" in {
    val list = 42:: false :: Some("OK") :: 42 :: HNil
    val tail: HVector = list.tail
    assert(tail == false :: Some("OK") :: 42 :: HNil)
  }

  "Select type" should "be found" in {
    val list: HVector = Some(2) :: false :: 42 :: Some("NOK") :: HNil
    val s: Option[Int] = select[Int](list)
    assert(s.contains(42))
  }

  "Select type with args" should "be found" in {
    val list = Container(2) :: false :: Container("OK") :: 42 :: HNil
    val s: Option[Container[String]] = select[Container[String]](list)
    assert(s.contains(Container("OK")))
  }

  "Type with args" should "be found" in {
    val list = Container(Container(1)) :: false :: Container(Container("OK")) :: HNil
    val s = select[Container[Container[String]]](list)
    assert(s.contains(Container(Container("OK"))))
  }

  "Double types check" should "be found" in {
    val list = DoubleElement(2, 1) :: false :: DoubleElement("OK", 1) :: HNil
    val s = select[DoubleElement[String, Int]](list)
    assert(s.contains(DoubleElement("OK", 1)))
  }

  "HNil" should "equals HNil" in {
    val x = HNil
    val y = HNil
    assert(x == y)
  }

  "HVector" should "equals same HVector" in {
    val x = "a" :: 1 :: HNil
    val y = "a" :: 1 :: HNil
    assert(x == y)
  }

  "HVector" should "be combine with simple HVector" in {
    val x = "a" :: 1 :: HNil
    val y = "b" :: HNil
    val expect = "b" :: "a" :: 1 :: HNil
    assert(y ++ x == expect)
  }

  "HVector" should "be combine HVector with HNil" in {
    val x = "a" :: 1 :: HNil
    val y = HNil
    val expect = "a" :: 1 :: HNil
    assert(x ++ y == expect)
    assert(y ++ x == expect)
  }

  "HVector" should "be combine HNil with HNil" in {
    val x = HNil
    val y = HNil
    val expect = HNil
    assert(y ++ x == expect)
  }

  "HVector" should "be combine with other HVector" in {
    val x = "a" :: 1 :: HNil
    val y = "b" :: 2 :: HNil
    val expect = "b" :: 2 :: "a" :: 1 :: HNil
    assert(y ++ x == expect)
  }
}
