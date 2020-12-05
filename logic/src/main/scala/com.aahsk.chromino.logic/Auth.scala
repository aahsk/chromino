package com.aahsk.chromino.logic

import java.util.UUID
import com.aahsk.chromino.domain.AnonymousUser

object Auth {
  def createAnonymousUser(nick: String): AnonymousUser =
    AnonymousUser(None, nick, UUID.randomUUID.toString)
}
