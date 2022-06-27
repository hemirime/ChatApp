package com.github.hemirime.chatapp
package chat.storage

import chat.{Chat, ChatStorage}
import user.User

import java.util.UUID
import scala.concurrent.Future

class InMemoryChatStorage extends ChatStorage {
  private var chats: List[Chat] = List.empty

  override def get(id: UUID): Future[Option[Chat]] =
    Future.successful(chats.find(_.id == id))

  override def get(name: String): Future[Option[Chat]] =
    Future.successful(chats.find(_.name == name))

  override def getAll: Future[Seq[Chat]] =
    Future.successful(chats)

  override def getAll(userId: User.ID): Future[Seq[Chat]] =
    Future.successful(chats.filter(_.users contains userId))

  override def save(chat: Chat): Future[Option[Chat]] =
    Future.successful {
      if (chats.exists(_.name == chat.name)) {
        None
      } else {
        chats = chat +: chats
        Some(chat)
      }
    }
}