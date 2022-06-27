package com.github.hemirime.chatapp

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, OWrites, Writes}

final case class Error(error: String)
final case class Ok[A](result: A)

object Response extends PlayJsonSupport {
  def ok[A](obj: A): Ok[A] = Ok(obj)
  def err(message: String): Error = Error(message)

  implicit val error: OWrites[Error] = Json.writes[Error]
  implicit def success[A](implicit w: Writes[A]): OWrites[Ok[A]] = Json.writes[Ok[A]]
}