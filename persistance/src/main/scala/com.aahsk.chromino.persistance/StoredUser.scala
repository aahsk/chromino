package com.aahsk.chromino.persistance

import com.aahsk.chromino.domain.{AnonymousUser, Indexed, LocalUser, User}

case class StoredUser(isAnon: Boolean, user: User)
object StoredUser {
  implicit val indexedStoredUser: Indexed[StoredUser] =
    new Indexed[StoredUser] {
      override def updatedID(entity: StoredUser, id: Int): StoredUser =
        entity.copy(user = entity.user.updatedID(id))
      override def id(entity: StoredUser): Option[Int] = entity.user.id
    }

  def fromUser(user: User) =
    user match {
      case u: LocalUser     => StoredUser(isAnon = false, user = u)
      case u: AnonymousUser => StoredUser(isAnon = true, user = u)
    }
}
