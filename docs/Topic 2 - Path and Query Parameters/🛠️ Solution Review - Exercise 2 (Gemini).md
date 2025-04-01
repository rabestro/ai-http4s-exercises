---
author:
  - Gemini
---
Excellent work! Let's review your solution for "Hands-on Exercise 2: Path and Query Parameters in http4s."

## 🛠️ Solution Review: Exercise 2

Your code effectively addresses the requirements of the exercise. Here's a breakdown of its strengths and a few minor points to consider:

**Strengths:**

- **Correct Implementation:**
    - You've correctly implemented the GET routes for `/users/{userId}`, `/articles/{articleId}`, and `/products` as specified in the exercise.
    - The UUID path parameter is correctly handled with error handling for invalid UUIDs.
    - The LocalDate query parameter is correctly handled with error handling for invalid dates.
    - The optional page parameter is correctly handled with a default value of 1.
- **http4s and Cats Effect Usage:**
    - You've appropriately used `HttpRoutes.of[IO]`, `EmberServerBuilder`, and Cats Effect's `IO` monad.
    - `orNotFound` is used, to provide a default response.
- **Error Handling:**
    - You've implemented error handling for invalid UUIDs and LocalDate query parameters, returning 400 Bad Request responses.
- **Logging:**
    - You've included Log4cats for logging, which is excellent practice.
- **Clear and Concise:**
    - The code is easy to read and understand.
- **Query Parameter Handling:**
    - You correctly used `QueryParamDecoder`, and `QueryParamDecoderMatcher`

**Minor Points:**

- **Host and Port:**
    - As in the previous exercise, it's generally good practice to explicitly set the host and port in your `EmberServerBuilder`.
- **UUID Handling:**
    - Using `Try(UUID.fromString(userId)).toEither` is a good way to handle the possible exception, but consider using `Either.catchOnly[IllegalArgumentException](UUID.fromString(userId))` for more precise exception handling.
- **Code Organization:**
    - You've included the previous exercises `/hello` and `/echo` endpoints. If you are only testing the current exercise, those endpoints are not needed.

**Corrected Code (with minor improvements):**

Scala

```scala
package exercise2

import cats.data.Validated
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import com.comcast.ip4s._

import java.time.LocalDate
import java.util.UUID
import scala.util.Try

object Solution extends IOApp {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory.create[IO]

  implicit val localDateDecoder: QueryParamDecoder[LocalDate] = param =>
    Validated
      .fromTry(scala.util.Try(LocalDate.parse(param.value)))
      .leftMap(t => ParseFailure(s"Invalid LocalDate: ${t.getMessage}", t.getMessage))
      .toValidatedNel
  object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")
  object PageMatcher extends OptionalQueryParamDecoderMatcher[Int]("page")

  private object CategoryMatcher extends QueryParamDecoderMatcher[String]("category")

  private val httpRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      IO.fromEither(Either.catchOnly[IllegalArgumentException](UUID.fromString(userId)))
        .flatMap(userId => Ok(s"User ID: $userId"))
        .handleErrorWith(_ => BadRequest("Invalid UUID format"))
    case GET -> Root / "articles" / IntVar(articleId) =>
      Ok(s"Article ID: $articleId")
    case GET -> Root / "products" :? CategoryMatcher(category) +& DateMatcher(date) +& PageMatcher(page) =>
      Ok(s"Category: $category, Date: $date, Page: ${page.getOrElse(1)}")
  }

  private val loggedRoutes = Logger.httpApp(
    logHeaders = true,
    logBody = true
  )(httpRoutes.orNotFound)

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(loggedRoutes)
      .build
      .useForever
      .as(ExitCode.Success)
}
```

**Overall:**

Your solution is excellent and demonstrates a strong understanding of path and query parameters in http4s. The suggested improvements are minor and aimed at enhancing clarity and best practices.

Do you have any questions or would you like to proceed to the next exercise?