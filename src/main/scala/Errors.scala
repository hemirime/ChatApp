package com.github.hemirime.chatapp

import chat.Chat
import user.User

sealed trait UserError

case class ChatNameAlreadyTaken(name: String) extends UserError

case class ChatNotFound(chatId: Chat.ID) extends UserError

case class UsersNotFound(userIds: Seq[User.ID]) extends UserError

case class UsernameAlreadyTaken(name: String) extends UserError