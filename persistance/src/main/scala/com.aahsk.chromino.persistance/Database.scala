package com.aahsk.chromino.persistance

import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.aahsk.chromino.domain.{Game, Indexed, User}
import com.aahsk.chromino.domain.Indexed.ops._
import com.aahsk.chromino.domain.User.UserID

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

  def getByID(id: Int): Option[A] = map.get(id)
}

object IndexedMap {
  def empty[A: Indexed](): IndexedMap[A] =
    IndexedMap(-1, Map())
}

case class Data(users: IndexedMap[StoredUser], games: IndexedMap[Game]) {
  def insertUser(user: User): (Data, User) =
    users.insert(StoredUser.fromUser(user)) match {
      case (newUsers, storedUser) => (copy(users = newUsers), storedUser.user)
    }

  def getUserByID(id: UserID): Option[User] = users.getByID(id).map(_.user)

  def getUserByNick(nick: String): Option[User] =
    users.map
      .find {
        case (_, storedUser) => storedUser.user.nick == nick
      }
      .map {
        case (_, storedUser) => storedUser.user
      }
}

object Data {
  def empty(): Data =
    Data(IndexedMap.empty[StoredUser](), IndexedMap.empty[Game]())
}
