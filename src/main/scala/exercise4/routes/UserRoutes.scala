package exercise4.routes

import cats.MonadThrow
import cats.data.Validated
import cats.effect._
import cats.syntax.all._
import exercise4.error.{AppError, UserNotFound}
import exercise4.model.{UserForm, UserFormValidator}
import exercise4.service.UserService
import io.circe.Json
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}

import java.util.UUID

class UserRoutes[F[_] : Concurrent](userService: UserService[F]) extends Http4sDsl[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "users" / userId =>
      parseUUID(userId).flatMap { id =>
        userService.getUser(id).value.flatMap {
          case Right(user) => Ok(user)
          case Left(error) => handleUserError(error)
        }
      }.handleErrorWith(handleUUIDError)

    case req@POST -> Root / "users" =>
      req.as[UserForm].flatMap { userForm =>
        UserFormValidator.validateUserForm(userForm) match {
          case Validated.Valid(validUser) =>
            userService.createUser(validUser).flatMap(createdUser => Created(createdUser))
          case Validated.Invalid(errors) =>
            BadRequest(Json.obj(
              "error" -> Json.fromString("Invalid User data"),
              "details" -> Json.fromValues(errors.toList.map(Json.fromString))
            ))
        }
      }.handleErrorWith(e => BadRequest(Json.obj("error" ->
        Json.fromString(s"Error processing request: ${e.getMessage}"))))
  }

  private def parseUUID(userId: String): F[UUID] =
    MonadThrow[F].fromEither(Either.catchNonFatal(UUID.fromString(userId))
      .leftMap(_ => new IllegalArgumentException("Invalid UUID format")))

  private def handleUserError(error: AppError): F[Response[F]] = error match {
    case _: UserNotFound => NotFound(Json.obj("error" -> Json.fromString("User not found")))
    case _ => InternalServerError(Json.obj("error" -> Json.fromString("Internal server error")))
  }

  private def handleUUIDError(error: Throwable): F[Response[F]] =
    BadRequest(Json.obj("error" -> Json.fromString("Invalid UUID format")))
}
