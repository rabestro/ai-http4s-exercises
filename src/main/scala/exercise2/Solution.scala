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

import java.time.LocalDate

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
    case GET -> Root / "users" / UUIDVar(userId) =>
      Ok(s"User ID: $userId")
    case GET -> Root / "users" / wrongId =>
      BadRequest(s"The userId is not a valid UUID: $wrongId")
    case GET -> Root / "articles" / IntVar(articleId) =>
      Ok(s"Article ID: $articleId")
    case GET -> Root / "products" :? CategoryMatcher(category) +& DateMatcher(date) +& PageMatcher(page) =>
      Ok(s"Category: $category, Date: $date, Page: ${page.getOrElse(1)}")
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")
    case req@POST -> Root / "echo" =>
      req.as[String].flatMap(body => Ok(body))
  }

  private val loggedRoutes = Logger.httpApp(
    logHeaders = true,
    logBody = true
  )(httpRoutes.orNotFound)

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(loggedRoutes)
      .build
      .useForever
      .as(ExitCode.Success)
}

