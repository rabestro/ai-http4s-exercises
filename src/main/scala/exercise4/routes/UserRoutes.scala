package exercise4.routes

import cats.MonadThrow
import cats.data.{Validated, ValidatedNel}
import cats.effect._
import cats.syntax.all._
import exercise4.error.{AppError, UserNotFound}
import exercise4.model.UserDetails
import exercise4.service.UserService
import io.circe.Json
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}

import java.util.UUID

class UserRoutes[F[_]: Concurrent](userService: UserService[F]) {
  val dsl = Http4sDsl[F]
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "users" / userId =>
      parseUUID(userId).flatMap { id =>
        userService.getUser(id).value.flatMap {
          case Right(user) => Ok(user)
          case Left(error) => handleUserError(error)
        }
      }.handleErrorWith(handleUUIDError)

    case req@POST -> Root / "users" =>
      req.as[UserDetails].flatMap { user =>
        validateUser(user) match {
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

  def validateUser(user: UserDetails): ValidatedNel[String, UserDetails] = {
    val nameValidation: ValidatedNel[String, String] =
      if (user.name.nonEmpty) user.name.validNel
      else "Name cannot be empty".invalidNel
    val ageValidation: ValidatedNel[String, Int] =
      if (user.age > 0) user.age.validNel
      else "Age must be positive".invalidNel

    (nameValidation, ageValidation).mapN((_, _) => user)
  }
}
