package com.github.hemirime.chatapp
package user.storage

import user.{User, UserStorage}

import slick.jdbc.{JdbcBackend, JdbcProfile}

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.Future

class DatabaseUserStorage(val profile: JdbcProfile, val db: JdbcBackend#Database) extends UserStorage {
  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def createdAt = column[OffsetDateTime]("created_at")
    def * = (id, username, createdAt).shaped <> ((User.apply _).tupled, User.unapply)
  }
  private val users = TableQuery[Users]

  def createSchema: Future[Unit] =
    db.run(users.schema.createIfNotExists)

  override def get(id: UUID): Future[Option[User]] =
    db.run(users.filter(_.id === id).take(1).result.headOption)

  override def get(name: String): Future[Option[User]] =
    db.run(users.filter(_.username === name).take(1).result.headOption)

  override def getAll: Future[Seq[User]] =
    db.run(users.result)

  override def save(user: User): Future[User] =
    db.run((users returning users.map(_.id) into((_,_) => user)) += user)
}