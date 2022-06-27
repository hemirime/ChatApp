package com.github.hemirime.chatapp
package chat.api

import Response._
import chat.ChatService

import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import java.util.UUID

class ChatApi(chatService: ChatService)
  extends EntityMarshalling {

  lazy val routes: Route = pathEnd {
    concat(
      getChats,
      createChat,
    )
  }

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
}
