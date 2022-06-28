package com.github.hemirime.chatapp
package chat

import user.{User, UserStorage}

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ChatService(chatStorage: ChatStorage,
                  messageStorage: MessageStorage,
                  userStorage: UserStorage)
                 (implicit ec: ExecutionContext) {

  def createChat(name: String, users: Seq[User.ID]): Future[Either[UserError, Chat]] = {
    Future.traverse(users) { id =>
      userStorage.get(id).map(id -> _)
    } flatMap {
      case foundUsers if foundUsers.flatMap(_._2).length == users.length =>
        val chat = Chat(UUID.randomUUID(), name, users, OffsetDateTime.now())
        chatStorage.save(chat)
          .map(Right.apply)
          .recover {
            case _: Throwable => Left(ChatNameAlreadyTaken(name))
          }
      case list => Future.successful(Left(UsersNotFound(list.filter(_._2.isEmpty).map(_._1))))
    }
  }

  def getChats(userId: User.ID): Future[Option[Seq[Chat]]] =
    ifExist(userStorage.get(userId)) { _ =>
      chatStorage.getAll(userId)
    }

  def getAllChats: Future[Seq[Chat]] =
    chatStorage.getAll

  def getMessages(chatId: Chat.ID): Future[Option[Seq[Message]]] = {
    ifExist(chatStorage.get(chatId)) { _ =>
      messageStorage.getAll(chatId)
    }
  }

  def sendMessage(chatId: Chat.ID, author: User.ID, text: String): Future[Option[Message]] =
    ifExist(chatStorage.get(chatId)) { _ =>
      val message = Message(UUID.randomUUID(), chatId, author, text, OffsetDateTime.now())
      messageStorage.add(message)
    }

  private def ifExist[T, S](t: Future[Option[T]])(f: T => Future[S]): Future[Option[S]] =
    t flatMap {
      case Some(value) => f(value).map(Some.apply)
      case _ => Future.successful(None)
    }
}
