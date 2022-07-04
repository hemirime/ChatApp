package com.github.hemirime.chatapp
package user.storage

import user.{User, UserStorage}

import java.util.UUID
import scala.concurrent.Future

class DatabaseUserStorage(dal: UserComponent) extends UserStorage {
  import dal._
  import dal.profile.api._

  override def get(id: UUID): Future[Option[User]] =
    db.run(UsersTable.filter(_.id === id).take(1).result.headOption)

  override def get(name: String): Future[Option[User]] =
    db.run(UsersTable.filter(_.username === name).take(1).result.headOption)

  override def getAll: Future[Seq[User]] =
    db.run(UsersTable.result)

  override def save(user: User): Future[User] =
    db.run((UsersTable returning UsersTable.map(_.id) into((_, _) => user)) += user)
}