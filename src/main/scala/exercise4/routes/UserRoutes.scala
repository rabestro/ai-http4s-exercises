package exercise4.routes

import cats.data.{Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits._
import exercise4.error.{AppError, UserNotFound}
import exercise4.model.UserDetails
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

  private def parseUUID(userId: String): IO[UUID] =
    IO.fromEither(Either.catchNonFatal(UUID.fromString(userId))
      .leftMap(_ => new IllegalArgumentException("Invalid UUID format")))

  private def handleUserError(error: AppError): IO[Response[IO]] = error match {
    case _: UserNotFound => NotFound(Json.obj("error" -> Json.fromString("User not found")))
    case _ => InternalServerError(Json.obj("error" -> Json.fromString("Internal server error")))
  }

  private def handleUUIDError(error: Throwable): IO[Response[IO]] =
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
