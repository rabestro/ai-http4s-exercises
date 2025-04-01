The issue you're encountering is due to the way `http4s` handles query parameter decoding. When a query parameter fails to decode, `http4s` returns a `404 Not Found` response instead of a `400 Bad Request`. This behavior is by design, as `404` is more semantically correct for missing or malformed query parameters.

However, you can customize this behavior by creating a custom `QueryParamDecoder` that returns a `400 Bad Request` response when a query parameter fails to decode. Here's how you can do it:

### Custom Query Parameter Decoder for LocalDate

You can create a custom `QueryParamDecoder` for `LocalDate` that returns a `400 Bad Request` response when the date is invalid. This involves creating a custom `QueryParamDecoder` and using it in your route definitions.

#### Custom Query Parameter Decoder:
```scala src/main/scala/exercise2/decoders/LocalDateDecoder.scala
package exercise2.decoders

import cats.data.Validated
import org.http4s.ParseFailure
import org.http4s.QueryParamDecoder

import java.time.LocalDate

object LocalDateDecoder {
  implicit val localDateDecoder: QueryParamDecoder[LocalDate] = param =>
    Validated
      .fromTry(scala.util.Try(LocalDate.parse(param.value)))
      .leftMap(t => ParseFailure(s"Invalid LocalDate: ${t.getMessage}", t.getMessage))
      .toValidatedNel
}
```

#### Updated Route Definitions:
```scala src/main/scala/exercise2/routes/Routes.scala
package exercise2.routes

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

import java.time.LocalDate
import java.util.UUID
import scala.util.Try

import exercise2.decoders.LocalDateDecoder._

object Routes {
  object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")
  object PageMatcher extends OptionalQueryParamDecoderMatcher[Int]("page")
  private object CategoryMatcher extends QueryParamDecoderMatcher[String]("category")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      IO.fromEither(Try(UUID.fromString(userId)).toEither)
        .flatMap(userId => Ok(s"User ID: $userId"))
        .handleErrorWith(_ => BadRequest("Invalid UUID format"))
    case GET -> Root / "articles" / IntVar(articleId) =>
      Ok(s"Article ID: $articleId")
    case GET -> Root / "products" :? CategoryMatcher(category) +& DateMatcher(date) +& PageMatcher(page) =>
      Ok(s"Category: $category, Date: $date, Page: ${page.getOrElse(1)}")
  }
}
```

#### Main Application:
```scala src/main/scala/exercise2/Solution.scala
package exercise2

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

import exercise2.routes.Routes

object Solution extends IOApp {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory.create[IO]

  private val loggedRoutes = Logger.httpApp(
    logHeaders = true,
    logBody = true
  )(Routes.routes.orNotFound)

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

### Explanation:
1. **Custom Query Parameter Decoder**:
   - The `LocalDateDecoder` object contains an implicit `QueryParamDecoder` for `LocalDate` that returns a `400 Bad Request` response when the date is invalid.

2. **Route Definitions**:
   - The `DateMatcher` object uses the custom `QueryParamDecoder` for `LocalDate`.
   - The route for `/products` uses the `DateMatcher` to decode the `date` query parameter.

3. **Main Application**:
   - The main application remains the same, but it now uses the updated route definitions.

By using a custom `QueryParamDecoder` for `LocalDate`, you can ensure that invalid dates return a `400 Bad Request` response instead of a `404 Not Found` response. This makes the error handling more consistent and semantically correct.