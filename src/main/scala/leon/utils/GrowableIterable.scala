/* Copyright 2009-2016 EPFL, Lausanne */

package leon.utils

import scala.collection.mutable.ArrayBuffer

class GrowableIterable[T](init: Seq[T], growth: Iterator[T]) extends Iterable[T] {
  private var buffer_ = new ArrayBuffer[T]() ++ init

  var canGrow = () => true

  private val cachingIterator = new Iterator[T] {
    def hasNext = canGrow() && growth.hasNext

    def next() = {
      val res = growth.next()
      buffer_ += res
      res
    }
  }

  def += (more: T)           = buffer_ +=  more
  def ++=(more: Iterable[T]) = buffer_ ++= more
  def -= (less: T)           = buffer_ -=  less
  def --=(less: Iterable[T]) = buffer_ --= less

  def buffer = buffer_

  def iterator: Iterator[T] = {
    buffer.iterator ++ cachingIterator
  }


  def bufferedCount = buffer.size

  def sortBufferBy[B](f: T => B)(implicit ord: math.Ordering[B]) = {
    buffer_ = buffer_.sortBy(f)
  }

  def map[B](f: T => B): GrowableIterable[B] = {
    new GrowableIterable(buffer.map(f), growth.map(f))
  }
}
