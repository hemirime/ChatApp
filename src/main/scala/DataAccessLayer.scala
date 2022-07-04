package com.github.hemirime.chatapp

import chat.storage.{ChatComponent, MessageComponent}
import data.DbComponent
import user.storage.UserComponent

import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

class DataAccessLayer(override val profile: JdbcProfile, override val db: JdbcBackend#Database) extends DbComponent
  with UserComponent
  with ChatComponent
  with MessageComponent {

  import profile.api._

  def init: Future[Unit] = {
    val createSchema = Seq(
      UsersTable,
      ChatsTable,
      UserChatsTable,
      MessagesTable,
    ).map(_.schema)
      .reduce(_ ++ _)
      .createIfNotExists
    db.run(createSchema)
  }

}