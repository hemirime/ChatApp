package com.github.hemirime.chatapp
package chat.storage

import chat.Chat.ID
import chat.{Chat, Message, MessageStorage}

import scala.concurrent.{ExecutionContext, Future}

class DatabaseMessageStorage(dal: MessageComponent)(implicit ec: ExecutionContext) extends MessageStorage {
  import dal._
  import profile.api._

  override def add(message: Message): Future[Message] =
    db.run((MessagesTable returning MessagesTable.map(_.id) into((_, _) => message)) += new MessageDB(message))

  override def getAll(chatId: ID): Future[Seq[Message]] =
    db.run(ChatsTable.selectQuery.filter { case (c, u) => c.id === chatId }.result
      .flatMap { chatRes =>
        val c = chatRes.head._1
        val u = chatRes.map(_._2)
        val chat = Chat(c.id, c.name, u, c.createdAt)
        MessagesTable
          .join(UsersTable).on { case (m, u) => m.userId === u.id }
          .join(ChatsTable).on { case ((m, u), c) => m.chatId === c.id }
          .filter { case (_, c) => c.id === chatId }
          .map { case (um, _) => um }
          .result
          .map(_.map { case (m, u) => Message(m.id, chat, u, m.text, m.createdAt) })
      })

}