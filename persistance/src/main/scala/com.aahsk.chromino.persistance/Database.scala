package com.aahsk.chromino.persistance

import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.aahsk.chromino.domain.{Game, Indexed, User}
import com.aahsk.chromino.domain.Indexed.ops._

object Database {
  type Database[F[_]] = Ref[F, Data]
  def empty[F[_]: Sync](): F[Database[F]] = Ref.of[F, Data](Data.empty())
}

case class IndexedMap[A: Indexed](
    lastIndex: Int,
    map: Map[Int, A]
) {
  def insert(entity: A): (IndexedMap[A], A) = {
    val newIndex = lastIndex + 1
    val indexifiedEntity = entity.updatedID(newIndex)
    (
      copy(lastIndex = newIndex, map = map.updated(newIndex, indexifiedEntity)),
      indexifiedEntity
    )
  }
}

object IndexedMap {
  def empty[A: Indexed](): IndexedMap[A] =
    IndexedMap(-1, Map())
}

case class Data(users: IndexedMap[User], games: IndexedMap[Game]) {
  def insertUser(user: User): (Data, User) =
    users.insert(user) match {
      case (newUsers, newUser) => (copy(users = newUsers), newUser)
    }
}

object Data {
  def empty(): Data =
    Data(IndexedMap.empty[User](), IndexedMap.empty[Game]())
}
