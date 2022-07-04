package com.github.hemirime.chatapp
package chat.storage

import chat.Chat
import data.DbComponent
import user.User
import user.storage.UserComponent

import java.time.OffsetDateTime
import java.util.UUID

trait ChatComponent extends DbComponent with UserComponent {
  import profile.api._

  final case class ChatDB(id: Chat.ID, name: String, createdAt: OffsetDateTime) {
    def this(chat: Chat) = this(chat.id, chat.name, chat.createdAt)
  }

  class Chats(tag: Tag) extends Table[ChatDB](tag, "chats") {
    def id = column[UUID]("id", O.PrimaryKey)
    def name = column[String]("name", O.Unique)
    def createdAt = column[OffsetDateTime]("created_at")
    def * = (id, name, createdAt).shaped <> (ChatDB.tupled, ChatDB.unapply)
  }

  final case class UserChatRelation(id: UUID, userId: User.ID, chatId: Chat.ID)

  class UserChatRelations(tag: Tag) extends Table[UserChatRelation](tag, "user_chats") {
    def id = column[UUID]("id", O.PrimaryKey)

    def userId = column[UUID]("user_id")
    def user = foreignKey("user_fk", userId, UsersTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def chatId = column[UUID]("chat_id")
    def chat = foreignKey("chat_fk", chatId, ChatsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def * = (id, userId, chatId).shaped <> ((UserChatRelation.apply _).tupled, UserChatRelation.unapply)
  }

  lazy val UserChatsTable = TableQuery[UserChatRelations]

  object ChatsTable extends TableQuery(new Chats(_)) {
    val selectQuery = this
      .join(UserChatsTable).on { case (c, ucr) => c.id === ucr.chatId }
      .join(UsersTable).on { case ((_, ucr), u) => ucr.userId === u.id }
      .map { case ((c, _), u) => (c, u) }
  }

}
