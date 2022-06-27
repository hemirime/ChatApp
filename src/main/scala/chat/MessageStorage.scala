package com.github.hemirime.chatapp
package chat

import scala.concurrent.Future

trait MessageStorage {
  def add(message: Message): Future[Message]
  def getAll(chatId: Chat.ID): Future[Seq[Message]]
}
