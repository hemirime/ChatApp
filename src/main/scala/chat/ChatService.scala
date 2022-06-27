package com.github.hemirime.chatapp
package chat

import user.User

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.Future

class ChatService(chatStorage: ChatStorage) {

  def createChat(name: String, users: Seq[User.ID]): Future[Option[Chat]] = {
    val chat = Chat(UUID.randomUUID(), name, users, OffsetDateTime.now())
    chatStorage.save(chat)
  }

  def getChats(user: User.ID): Future[Seq[Chat]] =
    chatStorage.getAll(user)

  def getAllChats: Future[Seq[Chat]] =
    chatStorage.getAll

}
