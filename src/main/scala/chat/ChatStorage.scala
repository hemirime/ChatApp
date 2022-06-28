package com.github.hemirime.chatapp
package chat

import user.User

import java.util.UUID
import scala.concurrent.Future

trait ChatStorage {
  def get(id: UUID): Future[Option[Chat]]

  def get(name: String): Future[Option[Chat]]

  def getAll: Future[Seq[Chat]]

  def getAll(userId: User.ID): Future[Seq[Chat]]

  def save(chat: Chat): Future[Chat]
}