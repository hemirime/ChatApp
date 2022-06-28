package com.github.hemirime.chatapp
package user

import java.util.UUID
import scala.concurrent.Future

trait UserStorage {
  def get(id: UUID): Future[Option[User]]

  def get(name: String): Future[Option[User]]

  def getAll: Future[Seq[User]]

  def save(user: User): Future[User]
}