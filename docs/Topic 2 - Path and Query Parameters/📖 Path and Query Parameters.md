👨🏻‍🎓 Fine! I'm ready for the next challenge

🤖 Excellent! Let's move on to the next topic: **Path and Query Parameters**.

## 📖 Theoretical Explanation: Path and Query Parameters

In HTTP, URLs can contain parameters that provide additional information for the server process. There are two main types of URL parameters:

- **Path Parameters:**
    - These are parts of the URL path itself.
    - They are typically used to identify specific resources.
    - Example: `/users/123` (where `123` is the user ID).
- **Query Parameters:**
    - These are key-value pairs appended to the URL after a question mark (`?`).
    - They are typically used to filter, sort, or provide additional options.
    - Example: `/products?category=electronics&sort=price`

**http4s Handling:**

- **Path Parameters:**
    - http4s allows you to extract path parameters using pattern matching in your routes.
    - You can define custom extractors to convert path parameters to specific types.
- **Query Parameters:**
    - http4s provides `QueryParamDecoderMatcher` to extract query parameters.
    - You need to provide implicit `QueryParamDecoder` instances for the parameter types.

**Key Concepts:**

- **Extractors:** Objects that define how to extract values from strings.
- **`QueryParamDecoder`:** Type class that defines how to decode query parameters.
- **`QueryParamDecoderMatcher`:** Object that uses a `QueryParamDecoder` to match query parameters.

Do you understand these concepts, or do you have any questions before we proceed to the exercise?

👨🏻‍🎓 Please provide some code samples.

🤖 Certainly! Let's illustrate path and query parameters with some code examples.

## 📖 Code Samples: Path and Query Parameters in http4s

**1. Path Parameters:**

Scala

```scala
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import java.util.UUID

object PathParams {

  // Custom extractor for UUID
  object UUIDVar {
    def unapply(value: String): Option[UUID] = {
      try {
        Some(UUID.fromString(value))
      } catch {
        case _: IllegalArgumentException => None
      }
    }
  }

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / UUIDVar(userId) =>
      Ok(s"User ID: $userId")

    case GET -> Root / "articles" / IntVar(articleId) =>
      Ok(s"Article ID: $articleId")

    case GET -> Root / "books" / LongVar(bookId) / "pages" / IntVar(pageNumber) =>
      Ok(s"Book ID: $bookId, Page: $pageNumber")

    case GET -> Root / "items" / name =>
      Ok(s"Item name: $name")
  }
}
```

- **`UUIDVar`:** A custom extractor to parse UUIDs from path segments.
- **`IntVar` and `LongVar`:** Built-in extractors for integers and longs.
- The last example shows how to get the `name` as a String.

**2. Query Parameters:**

Scala

```scala
import cats.data.ValidatedNel
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import java.time.LocalDate
import org.http4s.QueryParamDecoder
import org.http4s.ParseFailure

object QueryParams {

  // Custom QueryParamDecoder for LocalDate
  implicit val localDateDecoder: QueryParamDecoder[LocalDate] = { param =>
    ValidatedNel
      .fromTry(scala.util.Try(LocalDate.parse(param.value)))
      .leftMap(t => ParseFailure(s"Invalid LocalDate: ${t.getMessage}", t.getMessage))
  }

  object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")
  object CategoryMatcher extends QueryParamDecoderMatcher[String]("category")
  object PageMatcher extends OptionalQueryParamDecoderMatcher[Int]("page")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "products" :? CategoryMatcher(category) +& DateMatcher(date) +& PageMatcher(page) =>
      Ok(s"Category: $category, Date: $date, Page: ${page.getOrElse(1)}")
  }
}
```

- **`localDateDecoder`:** An implicit `QueryParamDecoder` for `LocalDate`.
- **`DateMatcher`, `CategoryMatcher`:** `QueryParamDecoderMatcher` instances for `LocalDate` and `String`.
- **`PageMatcher`:** An `OptionalQueryParamDecoderMatcher` for an optional `Int` parameter.
- The example shows how to use multiple query parameters, including an optional one.

These examples demonstrate how to extract and use path and query parameters in http4s routes. Do you have any questions about these examples?

