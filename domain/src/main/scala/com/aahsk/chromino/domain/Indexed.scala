package com.aahsk.chromino.domain

import scala.annotation.implicitNotFound

// Not using @typeclass annotation purely for educational purposes

@implicitNotFound("Could not find an instance of Indexed for ${A}")
trait Indexed[A] {
  def id(entity: A): Option[Int]
  def updatedID(entity: A, id: Int): A
}

object Indexed {
  def apply[A](implicit indexed: Indexed[A]): Indexed[A] = indexed
  object ops {
    def updatedID[A: Indexed](a: A, id: Int): A = Indexed[A].updatedID(a, id)
    implicit class IndexedOps[A: Indexed](a: A) {
      def updatedID(id: Int): A = Indexed[A].updatedID(a, id)
    }
  }
}
