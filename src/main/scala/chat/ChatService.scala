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
    } flatMap { result =>
      result.flatMap(_._2) match {
        case foundUsers if foundUsers.length == users.length =>
          val chat = Chat(UUID.randomUUID(), name, foundUsers, OffsetDateTime.now())
          chatStorage.save(chat)
            .map(Right.apply)
            .recover {
              case _: Throwable => Left(ChatNameAlreadyTaken(name))
            }
        case _ => Future.successful(Left(UsersNotFound(result.filter(_._2.isEmpty).map(_._1))))
      }
    }
  }

  def getChat(chatId: Chat.ID): Future[Option[Chat]] =
    chatStorage.get(chatId)

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

  def sendMessage(chatId: Chat.ID, author: User.ID, text: String): Future[Either[UserError, Message]] =
    chatStorage.get(chatId) flatMap {
      case Some(chat) => userStorage.get(author) flatMap {
        case Some(user) =>
          val message = Message(UUID.randomUUID(), chat, user, text, OffsetDateTime.now())
          messageStorage.add(message).map(Right.apply)
        case _ => Future.successful(Left(UsersNotFound(Seq(author))))
      }
      case _ => Future.successful(Left(ChatNotFound(chatId)))
    }

  private def ifExist[T, S](t: Future[Option[T]])(f: T => Future[S]): Future[Option[S]] =
    t flatMap {
      case Some(value) => f(value).map(Some.apply)
      case _ => Future.successful(None)
    }
}
