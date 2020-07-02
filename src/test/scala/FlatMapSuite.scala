package flatmap

import scala.collection._

import org.scalatest._
import funsuite._
import matchers._

class FlatMapSuite extends AnyFunSuite with should.Matchers {
  val pairs = Seq((5, "A"), (6, "C"), (1, "E"), (10, "F"))

  def createMaps() = {
    val map = immutable.TreeMap.from(pairs)
    val flatmap = FlatMap.from(pairs)
    (map, flatmap)
  }

  test("FlatMap.from() size") {
    val flatmap = FlatMap.from(pairs)
    flatmap.size shouldEqual (pairs.size)
    flatmap.nonEmpty shouldBe true
  }

  // TODO: it does not work, fix it
  //  diverging implicit expansion for type scala.math.Ordering[T1]
  // test("FlatMap iEmpty") {
  //   FlatMap.empty should be(empty)
  // }

  test("against.TreeMap iterator order") {
    val (map, flatmap) = createMaps()
    for (((k1, v1), (k2, v2)) <- map zip flatmap) {
      k1 shouldEqual k2
      v1 shouldEqual v2
    }
  }

  test("against.TreeMap iteratoprFrom") {
    val (map, flatmap) = createMaps()
    val (max, _) = pairs.maxBy {
      case (k, _) => k
    }
    val (min, _) = pairs.minBy {
      case (k, _) => k
    }

    val mid = (min + max) / 2

    map.iteratorFrom(max + 1).toSeq shouldEqual flatmap
      .iteratorFrom(max + 1)
      .toSeq
    map.iteratorFrom(min - 1).toSeq shouldEqual flatmap
      .iteratorFrom(min - 1)
      .toSeq
    map.iteratorFrom(mid).toSeq shouldEqual flatmap.iteratorFrom(mid).toSeq
  }

  test("against.TreeMap key access") {
    val (map, flatmap) = createMaps()
    for ((k, v) <- map) {
      v shouldEqual (flatmap(k))
    }
  }

  test("against.TreeMap update") {
    var (map, flatmap) = createMaps()
    val nonExistsKey = -99999
    val nonExistsValue = "DEADBEAF"

    map.contains(nonExistsKey) shouldBe (false)
    flatmap.contains(nonExistsKey) shouldBe (false)

    map = map.updated(nonExistsKey, nonExistsValue)
    flatmap = flatmap.updated(nonExistsKey, nonExistsValue)

    flatmap(nonExistsKey) shouldEqual (nonExistsValue)
    flatmap shouldEqual map
  }
}
