package exercise1

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Solution extends IOApp {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory.create[IO]

  private val httpRoutes = HttpRoutes.of[IO] {
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

