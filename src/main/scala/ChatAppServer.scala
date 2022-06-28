package com.github.hemirime.chatapp

import chat.ChatService
import chat.api.ChatApi
import chat.storage.{InMemoryChatStorage, InMemoryMessageStorage}
import user.UserService
import user.api.UserApi
import user.storage.InMemoryUserStorage

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object ChatAppServer extends JsonErrorHandling {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    ActorSystem[Nothing](ChatAppServer(host, port), "chat-app")
  }

  def apply(host: String, port: Int): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    implicit val system: ActorSystem[Nothing] = context.system
    import system.executionContext

    val userStorage = new InMemoryUserStorage
    val userApi = new UserApi(new UserService(userStorage)).routes
    val chatApi = new ChatApi(new ChatService(new InMemoryChatStorage, new InMemoryMessageStorage, userStorage)).routes

    val routes = Route.seal(
      concat(
        pathPrefix("users")(userApi),
        pathPrefix("chats")(chatApi),
      )
    )

    Http()
      .newServerAt(host, port)
      .bind(routes)
      .map(_.addToCoordinatedShutdown(3.seconds))
      .onComplete {
        case Success(binding) =>
          val address = binding.localAddress
          system.log.info("Server started at http://{}:{}/", address.getHostString, address.getPort)
        case Failure(exception) =>
          system.log.error("Failed to bing HTTP endpoint, terminating system", exception)
          system.terminate()
      }

    Behaviors.empty
  }
}
