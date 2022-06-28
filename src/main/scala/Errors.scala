package com.github.hemirime.chatapp

sealed trait UserError

case class UsernameAlreadyTaken(name: String) extends UserError