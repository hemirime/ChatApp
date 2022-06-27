package com.github.hemirime.chatapp
package chat

import user.User

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ChatService(chatStorage: ChatStorage,
                  messageStorage: MessageStorage)
                 (implicit ec: ExecutionContext){

  def createChat(name: String, users: Seq[User.ID]): Future[Option[Chat]] = {
    val chat = Chat(UUID.randomUUID(), name, users, OffsetDateTime.now())
    chatStorage.save(chat)
  }

  def getChats(userId: User.ID): Future[Seq[Chat]] =
    chatStorage.getAll(userId)

  def getAllChats: Future[Seq[Chat]] =
    chatStorage.getAll

  def getMessages(chatId: Chat.ID): Future[Seq[Message]] =
    messageStorage.getAll(chatId)

  def sendMessage(chatId: Chat.ID, author: User.ID, text: String): Future[Option[Message]] = {
    chatStorage.get(chatId) flatMap {
      case Some(_) =>
        val message = Message(UUID.randomUUID(), chatId, author, text, OffsetDateTime.now())
        messageStorage.add(message).map(Some[Message])
      case _ => Future.successful(None)
    }
  }
}
