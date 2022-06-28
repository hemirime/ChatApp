package com.github.hemirime.chatapp

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.{complete, rejectEmptyResponse}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Json, OWrites, Writes}

final case class Error(error: String)
final case class Ok[A](result: A)

object Response extends PlayJsonSupport {
  def ok[A](obj: A): Ok[A] = Ok(obj)
  def err(message: String): Error = Error(message)

  implicit val errorWrites: OWrites[Error] = Json.writes[Error]
  implicit def okWrites[A](implicit w: Writes[A]): OWrites[Ok[A]] = Json.writes[Ok[A]]

  def completeWithStatus[T](status: StatusCode, result: T)(implicit w: Writes[T]): Route =
    complete(status -> ok(result))

  def completeWithStatus[T](status: StatusCode, result: Option[T])(implicit w: Writes[T]): Route =
    rejectEmptyResponse {
      complete(result.map(status -> ok(_)))
    }

  def completeWithStatus[T](status: StatusCode, result: Either[UserError, T])(implicit w: Writes[T]): Route =
    result match {
      case Left(error) => error match {
        case ChatNameAlreadyTaken(name) => complete(BadRequest, err(s"chat with name '$name' already created"))
        case UsersNotFound(userIds) => complete(BadRequest, err(s"users with ids: '${userIds.mkString(", ")}' not found"))
        case UsernameAlreadyTaken(name) => complete(BadRequest, err(s"username '$name' already taken"))
      }
      case Right(value) => complete(status, ok(value))
    }

}
