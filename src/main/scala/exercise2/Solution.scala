package exercise2

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import exercise2.routes.Routes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

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
