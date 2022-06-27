package com.github.hemirime.chatapp
package user.api

import Response._
import user.UserService

import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class UserApi(userService: UserService)
  extends EntityMarshalling {

  lazy val routes: Route = concat(
    getAll,
    createUser
  )

  def getAll: Route = get {
    onSuccess(userService.getAll) { users =>
      complete(OK, ok(users))
    }
  }

  def createUser: Route = post {
    entity(as[UserCreateRequest]) { request =>
      onSuccess(userService.create(request.username)) {
        case Some(user) => complete(Created, ok(user.id))
        case None => complete(BadRequest, err("username already taken"))
      }
    }
  }
}