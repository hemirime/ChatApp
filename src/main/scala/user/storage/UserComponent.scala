package com.github.hemirime.chatapp
package user.storage

import data.DbComponent
import user.User

import java.time.OffsetDateTime
import java.util.UUID

trait UserComponent extends DbComponent {
  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def createdAt = column[OffsetDateTime]("created_at")

    def * = (id, username, createdAt).shaped <> ((User.apply _).tupled, User.unapply)
  }

  lazy val UsersTable = TableQuery[Users]
}