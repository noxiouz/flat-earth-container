package jmh

import scala.util.Random
import scala.collection.immutable.TreeMap
import flatmap.FlatMap

import java.util.concurrent.TimeUnit
import java.util.{TreeMap => JavaTreeMap}

import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Mode,
  OutputTimeUnit,
  State,
  Scope
}

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@State(Scope.Benchmark)
class FlatMapBencchmark {

  object Init {
    val keys = (1 to 10240).toIndexedSeq
    val pairs = keys.map(v => (v, s"$v"))

    val treeMap = TreeMap.from(pairs)
    val flatMap = FlatMap.from(pairs)
    val javaTreeMap = {
      val m = new JavaTreeMap[Int, String]()
      for ((k, v) <- pairs) {
        m.put(k, v)
      }
      m
    }

    val shuffledKeys = Random.shuffle(keys)

    val (kMin, kMax) = (keys.min, keys.max)
  }

  @Benchmark
  def lookupAllKeysTreeMap: Unit = {
    lookupAllKeys(Init.treeMap)
  }

  @Benchmark
  def lookupAllKeysFlatMap: Unit = {
    lookupAllKeys(Init.flatMap)
  }

  @Benchmark
  def lookupAllKeysJavaTreeMap: Unit = {
    for (k <- Init.shuffledKeys) {
      assert(Init.javaTreeMap.containsKey(k))
    }
  }

  @Benchmark
  def lookupSeveralKeysTreeMap: Unit = {
    lookupSeveralKeys(Init.treeMap)
  }

  @Benchmark
  def lookupSeveralKeysFlatMap: Unit = {
    lookupSeveralKeys(Init.flatMap)
  }

  private def lookupAllKeys[V](m: Map[Int, V]): Unit = {
    for (k <- Init.shuffledKeys) {
      assert(m.contains(k))
    }
  }

  private def lookupSeveralKeys[V](m: Map[Int, V]): Unit = {
    assert(m.contains(Init.kMin))
    assert(m.contains(Init.kMax))
  }
}
