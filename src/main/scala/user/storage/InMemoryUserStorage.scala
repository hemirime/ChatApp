package com.github.hemirime.chatapp
package user.storage

import user.{User, UserStorage}

import java.util.UUID
import scala.concurrent.Future

class InMemoryUserStorage extends UserStorage {
  private var users: List[User] = List.empty

  override def get(id: UUID): Future[Option[User]] =
    Future.successful(users.find(_.id == id))

  override def get(name: String): Future[Option[User]] =
    Future.successful(users.find(_.username == name))

  override def getAll: Future[Seq[User]] =
    Future.successful(users)

  override def save(user: User): Future[Option[User]] =
    Future.successful {
      if (users.exists(_.username == user.username)) {
        None
      } else {
        users = user +: users
        Some(user)
      }
    }
}
