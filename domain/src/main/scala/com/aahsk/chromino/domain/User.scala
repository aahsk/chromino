package com.aahsk.chromino.domain

import User._

sealed trait User {
  val id: Option[UserID]
  val nick: String
  def updatedID(id: UserID): User
}

object User {
  type UserID = Int

  implicit val indexedUser: Indexed[User] =
    new Indexed[User] {
      override def updatedID(entity: User, id: UserID): User =
        entity.updatedID(id)
      override def id(entity: User): Option[UserID] = entity.id
    }
}

case class AnonymousUser(id: Option[UserID], nick: String, secret: String)
    extends User {
  def updatedID(id: Int): User = copy(id = Some(id))
}

case class LocalUser(
    id: Option[UserID],
    nick: String,
    saltedPasswordHash: String
) extends User {
  def updatedID(id: Int): User = copy(id = Some(id))
}
