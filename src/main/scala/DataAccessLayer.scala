package com.github.hemirime.chatapp

import chat.storage.ChatComponent
import data.DbComponent
import user.storage.UserComponent

import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

class DataAccessLayer(override val profile: JdbcProfile, override val db: JdbcBackend#Database) extends DbComponent
  with UserComponent
  with ChatComponent {

  import profile.api._

  def init: Future[Unit] =
    db.run((UsersTable.schema ++ ChatsTable.schema ++ UserChatsTable.schema).createIfNotExists)

}