/* Copyright 2009-2016 EPFL, Lausanne */

import leon.annotation.extern

object Aliasing3 {

  case class A(var x: Int)
  case class B(a: A, y: Int)

  def updateB(b1: B, b2: B): Unit = {
    b1.a.x = 42
    b2.a.x = 41
  }

  def update(a1: A, a2: A, y: Int): Unit = {
    updateB(B(a2, y), B(a1, y)) // here is the issue with GenC
  } ensuring(_ => a2.x == 42 && a1.x == 41)

  def _main(): Int = {
    val a1 = A(11)
    val a2 = A(22)

    update(a1, a2, -1)

    a2.x - a1.x - 1
  } ensuring { _ == 0 }

  @extern
  def main(args: Array[String]): Unit = _main()
}

