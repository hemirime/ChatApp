package com.github.hemirime.chatapp
package user

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.Future

class UserService(userStorage: UserStorage) {

  def create(name: String): Future[Option[User]] = {
    val user = User(UUID.randomUUID(), name, OffsetDateTime.now())
    userStorage.save(user)
  }

  def get(id: UUID): Future[Option[User]] =
    userStorage.get(id)

  def getAll: Future[Seq[User]] =
    userStorage.getAll

}
