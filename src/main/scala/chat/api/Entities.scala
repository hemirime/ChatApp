package com.github.hemirime.chatapp
package chat.api

import chat.{Chat, Message}
import user.User

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, OWrites, Reads, __}

import java.util.UUID

final case class ChatCreateRequest(name: String, users: Seq[User.ID]) {
  require(name.nonEmpty, "chat name can't be empty")
  require(users.nonEmpty, "users list can't be empty")
}

final case class SendMessageRequest(author: User.ID, text: String) {
  require(text.nonEmpty, "text can't be empty")
}

private[api] trait EntityMarshalling extends PlayJsonSupport {
  implicit val chatCreateReads: Reads[ChatCreateRequest] = (
    (__ \ "name").read[String] and
      (__ \ "users").read[Seq[UUID]]
    ) (ChatCreateRequest.apply _)
  implicit val sendMessageReads: Reads[SendMessageRequest] = Json.reads[SendMessageRequest]

  implicit val chatWrites: OWrites[Chat] = Json.writes[Chat]
  implicit val messageWrites: OWrites[Message] = Json.writes[Message]
}
