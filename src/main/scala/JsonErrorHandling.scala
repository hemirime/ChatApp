package com.github.hemirime.chatapp

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}

trait JsonErrorHandling {

  implicit def rejectionHandler: RejectionHandler = RejectionHandler.default
    .mapRejectionResponse {
      case res@HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
        val message = ent.data.utf8String.replaceAll("\"", """\"""")
        res.withEntity(jsonError(message))
      case other => other
    }

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Throwable => ctx => {
        ctx.log.warning("Error during processing of {} request {} - '{}'.", ctx.request.method.value, ctx.request.uri, e.getMessage)
        ctx.request.discardEntityBytes(ctx.materializer)
        ctx.complete(HttpResponse(InternalServerError, entity = jsonError("Internal Server Error")))
      }
    }

  private def jsonError(message: String): HttpEntity.Strict = HttpEntity(
    ContentTypes.`application/json`, s"""{"error":"$message"}"""
  )
}
