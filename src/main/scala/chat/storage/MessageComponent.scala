package com.github.hemirime.chatapp
package chat.storage

import chat.{Chat, Message}
import user.User

import java.time.OffsetDateTime
import java.util.UUID

trait MessageComponent extends ChatComponent {
  import profile.api._

  final case class MessageDB(id: Message.ID, chatId: Chat.ID, userId: User.ID, text: String, createdAt: OffsetDateTime) {
    def this(msg: Message) = this(msg.id, msg.chat.id, msg.author.id, msg.text, msg.createdAt)
  }

  class Messages(tag: Tag) extends Table[MessageDB](tag, "messages") {
    def id = column[UUID]("id", O.PrimaryKey)
    def chatId = column[UUID]("chat_id")
    def chat = foreignKey("chat_fk", chatId, ChatsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def userId = column[UUID]("user_id")
    def user = foreignKey("user_fk", userId, UsersTable)(_.id)
    def text = column[String]("text")
    def createdAt = column[OffsetDateTime]("created_at")
    def * = (id, chatId, userId, text, createdAt).shaped <> (MessageDB.tupled, MessageDB.unapply)
  }

  lazy val MessagesTable = TableQuery[Messages]
}
