package com.aahsk.chromino.domain

import User._

sealed trait User {
  val id: UserID
  val nick: String
}

object User {
  type UserID = Int
}

case class AnonymousUser(id: UserID, nick: String, secret: String) extends User

case class LocalUser(id: UserID, nick: String, saltedPasswordHash: String)
    extends User
