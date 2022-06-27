package com.github.hemirime.chatapp
package user

import java.time.OffsetDateTime
import java.util.UUID

final case class User(id: User.ID, username: String, createdAt: OffsetDateTime)

object User {
  type ID = UUID
}
