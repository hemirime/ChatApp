package com.github.hemirime.chatapp
package user

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserService(userStorage: UserStorage)
                 (implicit ec: ExecutionContext) {

  def create(name: String): Future[Either[UserError, User]] = {
    val user = User(UUID.randomUUID(), name, OffsetDateTime.now())
    userStorage.save(user)
      .map(Right.apply)
      .recover {
        case _: Throwable => Left(UsernameAlreadyTaken(name))
      }
  }

  def get(id: UUID): Future[Option[User]] =
    userStorage.get(id)

  def getAll: Future[Seq[User]] =
    userStorage.getAll

}
