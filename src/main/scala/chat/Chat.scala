package com.github.hemirime.chatapp
package chat

import user.User

import java.time.OffsetDateTime
import java.util.UUID

final case class Chat(id: Chat.ID, name: String, users: Seq[User], createdAt: OffsetDateTime)

object Chat {
  type ID = UUID
}