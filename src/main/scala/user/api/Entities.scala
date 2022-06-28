package com.github.hemirime.chatapp
package user.api

import user.User

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, OWrites, Reads, __}

final case class UserCreateRequest(username: String) {
  require(username.nonEmpty, "username can't be empty")
}

private[api] trait EntityMarshalling extends PlayJsonSupport {
  implicit val createUserReads: Reads[UserCreateRequest] = (__ \ "username").read[String].map(UserCreateRequest)
  implicit val userWrites: OWrites[User] = Json.writes[User]
}
