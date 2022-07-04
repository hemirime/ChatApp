package com.github.hemirime.chatapp
package chat

import user.User

import java.time.OffsetDateTime
import java.util.UUID

final case class Message(id: Message.ID, chat: Chat, author: User, text: String, createdAt: OffsetDateTime)

object Message {
  type ID = UUID
}
