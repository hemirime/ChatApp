package com.github.hemirime.chatapp
package chat.storage

import chat.{Chat, Message, MessageStorage}
import user.User.ID

import scala.concurrent.Future

class InMemoryMessageStorage extends MessageStorage {
  private var messages: Map[Chat.ID, Seq[Message]] = Map.empty.withDefaultValue(Seq.empty)

  override def add(message: Message): Future[Message] =
    Future.successful {
      messages = messages + (message.chat -> (message +: messages(message.chat)))
      message
    }

  override def getAll(chatId: ID): Future[Seq[Message]] =
    Future.successful(messages(chatId))

}