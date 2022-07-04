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
    pathPrefix(JavaUUID) { chatId =>
      concat(
        getChat(chatId),
        path("messages") {
          concat(
            getMessages(chatId),
            sendMessage(chatId),
          )
        },
      )
    }
  )

  def getChats: Route = (get & parameter("user".as[UUID].?)) {
    case Some(userId) => onSuccess(chatService.getChats(userId))(completeWithOption(OK, _))
    case _ => onSuccess(chatService.getAllChats)(completeWithStatus(OK, _))
  }

  def getChat(chatId: Chat.ID): Route = (pathEnd & get) {
    onSuccess(chatService.getChat(chatId))(completeWithOption(OK, _))
  }

  def createChat: Route = (pathEnd & post) {
    entity(as[ChatCreateRequest]) { request =>
      onSuccess(chatService.createChat(request.name, request.users)) { chat =>
        completeWithEither(Created, chat.map(_.id))
      }
    }
  }

  def getMessages(chatId: Chat.ID): Route = get {
    onSuccess(chatService.getMessages(chatId))(completeWithOption(OK, _))
  }

  def sendMessage(chatId: Chat.ID): Route = post {
    entity(as[SendMessageRequest]) { request =>
      onSuccess(chatService.sendMessage(chatId, request.author, request.text))(completeWithOption(OK, _))
    }
  }
}
