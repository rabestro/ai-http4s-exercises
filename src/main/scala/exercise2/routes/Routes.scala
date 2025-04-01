package exercise2.routes

import cats.effect.IO
import exercise2.decoders.LocalDateDecoder._
import org.http4s._
import org.http4s.dsl.io._

import java.time.LocalDate
import java.util.UUID
import scala.util.Try

object Routes {
  private object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")

  private object PageMatcher extends OptionalQueryParamDecoderMatcher[Int]("page")

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