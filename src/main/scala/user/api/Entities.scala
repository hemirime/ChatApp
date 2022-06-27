package com.github.hemirime.chatapp
package user.api

import user.User

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, OWrites, Reads, __}

final case class UserCreateRequest(username: String) {
  require(username.nonEmpty, "username can't be empty")
}

private[api] trait EntityMarshalling extends PlayJsonSupport {
  implicit val userCreate: Reads[UserCreateRequest] = (__ \ "username").read[String].map(UserCreateRequest)
  implicit val user: OWrites[User] = Json.writes[User]
}
