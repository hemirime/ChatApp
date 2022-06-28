package com.github.hemirime.chatapp
package chat.api

import Response._
import chat.{Chat, ChatService}

import akka.http.scaladsl.model.StatusCodes.{Created, OK}
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

  def getChats: Route = (get & parameter("user".as[UUID].?)) {
    case Some(userId) => onSuccess(chatService.getChats(userId))(completeWithStatus(OK, _))
    case _ => onSuccess(chatService.getAllChats)(completeWithStatus(OK, _))
  }

  def createChat: Route = post {
    entity(as[ChatCreateRequest]) { request =>
      onSuccess(chatService.createChat(request.name, request.users)) { chat =>
        completeWithStatus(Created, chat.map(_.id))
      }
    }
  }

  def getMessages(chatId: Chat.ID): Route = get {
    onSuccess(chatService.getMessages(chatId))(completeWithStatus(OK, _))
  }

  def sendMessage(chatId: Chat.ID): Route = post {
    entity(as[SendMessageRequest]) { request =>
      onSuccess(chatService.sendMessage(chatId, request.author, request.text))(completeWithStatus(OK, _))
    }
  }
}
