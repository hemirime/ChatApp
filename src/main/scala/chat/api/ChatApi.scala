package com.github.hemirime.chatapp
package chat.api

import Response._
import chat.{Chat, ChatService}

import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, NotFound, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import java.util.UUID

class ChatApi(chatService: ChatService)
  extends EntityMarshalling {

  lazy val routes: Route = concat(
    pathEnd {
      concat(
        getChats,
        createChat,
      )
    },
    path(JavaUUID / "messages") { chatId =>
      concat(
        getMessages(chatId),
        sendMessage(chatId)
      )
    }
  )

  def getChats: Route = get {
    concat(
      parameter("user".as[UUID]) { userId =>
        onSuccess(chatService.getChats(userId)) { chats =>
          complete(OK, ok(chats))
        }
      },
      onSuccess(chatService.getAllChats) { chats =>
        complete(OK, ok(chats))
      }
    )
  }

  def createChat: Route = post {
    entity(as[ChatCreateRequest]) { request =>
      onSuccess(chatService.createChat(request.name, request.users)) {
        case Some(chat) => complete(Created, ok(chat.id))
        case None => complete(BadRequest, err(s"chat with name '${request.name}' already created"))
      }
    }
  }

  def getMessages(chatId: Chat.ID): Route = get {
    onSuccess(chatService.getMessages(chatId)) { messages =>
      complete(OK, ok(messages))
    }
  }

  def sendMessage(chatId: Chat.ID): Route = post {
    entity(as[SendMessageRequest]) { request =>
      onSuccess(chatService.sendMessage(chatId, request.author, request.text)) {
        case Some(message) => complete(OK, ok(message))
        case None => complete(NotFound, err(s"chat with id '$chatId' not found"))
      }
    }
  }
}
