package com.github.hemirime.chatapp
package user.api

import Response._
import user.UserService

import akka.http.scaladsl.model.StatusCodes.{Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class UserApi(userService: UserService)
  extends EntityMarshalling {

  lazy val routes: Route = concat(
    getAll,
    getUser,
    createUser
  )

  def getAll: Route = (get & pathEnd) {
    onSuccess(userService.getAll) { users =>
      complete(OK, ok(users))
    }
  }

  def getUser: Route = (get & path(JavaUUID)) { userId =>
    onSuccess(userService.get(userId))(completeWithStatus(OK, _))
  }

  def createUser: Route = post {
    entity(as[UserCreateRequest]) { request =>
      onSuccess(userService.create(request.username)) { user =>
        completeWithStatus(Created, user.map(_.id))
      }
    }
  }
}