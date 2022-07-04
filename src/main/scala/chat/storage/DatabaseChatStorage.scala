package com.github.hemirime.chatapp
package chat.storage

import chat.{Chat, ChatStorage}
import user.User.ID

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class DatabaseChatStorage(dal: ChatComponent)(implicit val ec: ExecutionContext) extends ChatStorage {
  import dal._
  import profile.api._

  override def get(id: UUID): Future[Option[Chat]] =
    db.run(ChatsTable.selectQuery.filter { case (c, u) => c.id === id }.result) map {
      case result@Seq((c, _), _*) => Some(Chat(c.id, c.name, result.map(_._2), c.createdAt))
      case _ => None
    }

  override def get(name: String): Future[Option[Chat]] =
    db.run(ChatsTable.selectQuery.filter { case (c, u) => c.name === name }.result) map {
      case result@Seq((c, _), _*) => Some(Chat(c.id, c.name, result.map(_._2), c.createdAt))
      case _ => None
    }

  override def getAll: Future[Seq[Chat]] =
    db.run(ChatsTable.selectQuery.result) map { rows =>
      rows.groupBy { case (c, u) => c }
        .view.mapValues(_.map { case (c, u) => u })
        .toSeq
        .map { case (c, u) => Chat(c.id, c.name, u, c.createdAt) }
    }

  override def getAll(userId: ID): Future[Seq[Chat]] =
    db.run(ChatsTable.selectQuery.filter { case (c, u) => c.id in UserChatsTable.filter(_.userId === userId).map(_.chatId) }.result)  map { rows =>
      rows.groupBy { case (c, u) => c }
        .view.mapValues(_.map { case (c, u) => u })
        .toSeq
        .map { case (c, u) => Chat(c.id, c.name, u, c.createdAt) }
    }

  override def save(chat: Chat): Future[Chat] =
    db.run((for {
      _ <- ChatsTable += new ChatDB(chat)
      _ <- UserChatsTable ++= chat.users.map(u => UserChatRelation(UUID.randomUUID(), u.id, chat.id))
    } yield chat).transactionally)
}
