package flatmap

import scala.collection.immutable._
import scala.collection.mutable.ReusableBuilder
import scala.collection.{SortedMapFactory, SortedMapFactoryDefaults}
import scala.collection.Searching.{Found, InsertionPoint}
import scala.collection.mutable

sealed class FlatMap[K, +V] private (
    // TODO: use ArraySeq
    private val keysArr: IndexedSeq[K],
    private val valuesArr: IndexedSeq[V]
)(implicit val ordering: Ordering[K])
    extends AbstractMap[K, V]
    with SortedMap[K, V]
    with StrictOptimizedSortedMapOps[K, V, FlatMap, FlatMap[K, V]]
    with SortedMapFactoryDefaults[K, V, FlatMap, Iterable, Map] {

  assert(
    keysArr.size == valuesArr.size,
    s"keysArr.size ${keysArr.size} must be equal to valueArr.size ${valuesArr.size}"
  )
  // TODO: check that keys and values are sorted

  def this()(implicit ordering: Ordering[K]) = this(null, null)(ordering)

  override def sortedMapFactory: SortedMapFactory[FlatMap] = FlatMap

  override def iterator: Iterator[(K, V)] = {
    keysArr.iterator zip valuesArr.iterator
  }

  override def get(key: K): Option[V] = {
    keysArr.search(key) match {
      case Found(index)      => Some(valuesArr(index))
      case InsertionPoint(_) => None
    }
  }

  override def removed(key: K): FlatMap[K, V] = {
    keysArr.search(key) match {
      case Found(foundIndex) => {
        // TODO: is there more effective remove?
        val newKeysArr = {
          val (a, b) = keysArr.splitAt(foundIndex)
          a concat b
        }
        val newValuesArr = {
          val (a, b) = valuesArr.splitAt(foundIndex)
          a concat b
        }
        new FlatMap(newKeysArr, newValuesArr)
      }
      case InsertionPoint(_) => this
    }
  }

  override def rangeImpl(from: Option[K], until: Option[K]): FlatMap[K, V] = {
    val start = from
      .map {
        case Found(index)          => index
        case InsertionPoint(index) => index
      }
      .getOrElse(0)

    val end = until
      .map {
        case Found(index)          => index
        case InsertionPoint(index) => index
      }
      .getOrElse(this.size)

    new FlatMap(keysArr.slice(start, end), valuesArr.slice(start, end))
  }

  override def iteratorFrom(start: K): Iterator[(K, V)] = {
    val index = keysArr.search(start) match {
      case Found(index)          => index
      case InsertionPoint(index) => index
    }
    // TODO: is there a more effective way to advance iterator?
    val kIt = keysArr.iterator.drop(index)
    val vIt = valuesArr.iterator.drop(index)
    kIt zip vIt
  }

  override def keysIteratorFrom(start: K): Iterator[K] = {
    val index = keysArr.search(start) match {
      case Found(index)          => index
      case InsertionPoint(index) => index
    }
    // TODO: is there a more effective way to advance iterator?
    val kIt = keysArr.iterator.drop(index)
    kIt
  }

  override def updated[V1 >: V](key: K, value: V1): FlatMap[K, V1] = {
    keysArr.search(key) match {
      case Found(index) =>
        new FlatMap(
          keysArr.updated(index, key),
          valuesArr.updated(index, value)
        )
      case InsertionPoint(insertIndex) => {
        val (keyHead, keyTail) = keysArr.splitAt(insertIndex)
        val (valueHead, valueTail) = valuesArr.splitAt(insertIndex)
        new FlatMap(
          keyHead ++: (key +: keyTail),
          valueHead ++: (value +: valueTail)
        )
      }
    }
  }

  override def knownSize: Int = keysArr.size

  override def maxBefore(key: K): Option[(K, V)] = {
    keysArr.search(key) match {
      case Found(index) => Some((keysArr(index), valuesArr(index)))
      case InsertionPoint(index) if index > 0 =>
        Some((keysArr(index - 1), valuesArr(index - 1)))
      case _ => None
    }
  }

  override protected[this] def className = "FlatMap"
}

object FlatMap extends SortedMapFactory[FlatMap] {

  def empty[K: Ordering, V]: FlatMap[K, V] =
    new FlatMap(IndexedSeq.empty, IndexedSeq.empty)

  def from[K, V](
      it: IterableOnce[(K, V)]
  )(implicit ordering: Ordering[K]): FlatMap[K, V] = {
    it match {
      case fm: FlatMap[K, V] if fm.ordering == ordering => fm
      case _ => {
        val m = TreeMap.from[K, V](it)(ordering)
        val keysArr = m.keys.toIndexedSeq
        val valuesArr = m.values.toIndexedSeq
        new FlatMap[K, V](keysArr, valuesArr)(ordering)
      }
    }
  }

  def newBuilder[K, V](
      implicit ordering: Ordering[K]
  ): ReusableBuilder[(K, V), FlatMap[K, V]] =
    new ReusableBuilder[(K, V), FlatMap[K, V]] {
      private[this] var tree: TreeMap[K, V] = TreeMap.empty

      def addOne(elem: (K, V)): this.type = {
        tree = tree.updated(elem._1, elem._2)
        this
      }

      override def addAll(xs: IterableOnce[(K, V)]): this.type = {
        val it = xs.iterator
        while (it.hasNext) {
          val (k, v) = it.next()
          tree = tree.updated(k, v)
        }
        this
      }

      def result(): FlatMap[K, V] =
        if (tree.isEmpty) FlatMap.empty else FlatMap.from(tree)

      def clear(): Unit = { tree = TreeMap.empty }
    }

  class OfLongKey[+V]
      extends FlatMap[Long, V](
        ArraySeq.empty,
        IndexedSeq.empty
      )

  class ofIntKey[+V]
      extends FlatMap[Int, V](
        ArraySeq.empty,
        IndexedSeq.empty
      )
}
