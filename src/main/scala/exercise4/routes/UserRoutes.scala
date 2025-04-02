package exercise4.routes

import cats.effect.IO
import cats.implicits._
import exercise4.error.{AppError, UserNotFound}
import exercise4.service.UserService
import io.circe.Json
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response}

import java.util.UUID

class UserRoutes(userService: UserService[IO]) {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      parseUUID(userId).flatMap { id =>
        userService.getUser(id).value.flatMap {
          case Right(user) => Ok(user)
          case Left(error) => handleUserError(error)
        }
      }.handleErrorWith(handleUUIDError)
  }

  private def parseUUID(userId: String): IO[UUID] =
    IO.fromEither(Either.catchNonFatal(UUID.fromString(userId)).leftMap(_ => new IllegalArgumentException("Invalid UUID format")))

  private def handleUserError(error: AppError): IO[Response[IO]] = error match {
    case _: UserNotFound => NotFound(Json.obj("error" -> Json.fromString("User not found")))
    case _ => InternalServerError(Json.obj("error" -> Json.fromString("Internal server error")))
  }

  private def handleUUIDError(error: Throwable): IO[Response[IO]] =
    BadRequest(Json.obj("error" -> Json.fromString("Invalid UUID format")))
}