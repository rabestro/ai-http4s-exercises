Certainly! As Scala Sage, I'm delighted to provide you with code snippets that illustrate error handling concepts in http4s.

## 📖 Code Samples: Error Handling in http4s

**1. Using `EitherT`:**

Scala

```scala
import cats.data.EitherT
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object EitherTExample {

  sealed trait AppError
  case class UserNotFound(userId: String) extends AppError
  case class DatabaseError(message: String) extends AppError

  def getUser(userId: String): EitherT[IO, AppError, String] = {
    // Simulate a database lookup
    if (userId == "123") {
      EitherT.rightT[IO, AppError](s"User $userId found")
    } else {
      EitherT.leftT[IO, String](UserNotFound(userId))
    }
  }

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      getUser(userId).value.flatMap {
        case Right(user) => Ok(user)
        case Left(UserNotFound(id)) => NotFound(s"User $id not found")
        case Left(DatabaseError(message)) => InternalServerError(s"Database error: $message")
      }
  }
}
```

- `EitherT` is used to represent a computation that can result in `AppError` or `String`.
- The `getUser` function simulates a database lookup and returns an `EitherT`.
- The route handles the `EitherT` result and returns appropriate HTTP responses.

**2. Using `ValidatedNel`:**

Scala

```scala
import cats.data.ValidatedNel
import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object ValidatedNelExample {

  case class UserData(name: String, age: Int)

  def validateUserData(name: String, age: String): ValidatedNel[String, UserData] = {
    val validatedName = if (name.nonEmpty) name.validNel else "Name cannot be empty".invalidNel
    val validatedAge = Either.catchOnly[NumberFormatException](age.toInt).toValidatedNel.leftMap(_.getMessage)

    (validatedName, validatedAge).mapN(UserData.apply)
  }

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "users" =>
      req.decode[UrlForm] { formData =>
        validateUserData(formData.values.getOrElse("name", Seq("")).head, formData.values.getOrElse("age", Seq("")).head).fold(
          errors => BadRequest(errors.toList.mkString(", ")),
          userData => Ok(s"User data validated: $userData")
        )
      }
  }
}
```

- `ValidatedNel` is used to accumulate validation errors for user data.
- The `validateUserData` function validates the name and age.
- The route handles the `ValidatedNel` result and returns a `BadRequest` response with error messages or an `Ok` response.

**3. Custom Error Responses:**

Scala

```scala
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object CustomErrorExample {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "error" =>
      val errorResponse = Response[IO](Status.BadRequest)
        .withEntity("Custom error message")
        .withContentType(MediaType.text.plain)

      IO.pure(errorResponse)
  }
}
```

- A custom error response with a 400 Bad Request status code, plain text body, and content type is created.

These examples demonstrate how to handle errors using `EitherT`, `ValidatedNel`, and custom error responses in http4s. Do you have any further questions?