package com.github.hemirime.chatapp
package data

import slick.jdbc.{JdbcBackend, JdbcProfile}

trait DbComponent {
  val profile: JdbcProfile
  val db: JdbcBackend#Database
}