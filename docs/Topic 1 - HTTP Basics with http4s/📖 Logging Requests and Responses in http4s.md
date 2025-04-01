You're absolutely right! Let's enhance your logging to include request and response details. As Scala Sage, I'll guide you through the process.

## 📖 Theoretical Explanation: Logging Requests and Responses in http4s

To log requests and responses in http4s, you can use middleware. Middleware is a component that intercepts HTTP requests and responses, allowing you to perform actions like logging.

Here's a breakdown:

- **Middleware:**
    - Middleware is a function that transforms an `HttpApp` into another `HttpApp`.
    - It allows you to add cross-cutting concerns like logging, authentication, and authorization.
    - http4s provides built-in middleware and allows you to create custom ones.
- **Logging Middleware:**
    - You can create middleware that logs request and response details.
    - This middleware can log information like the request method, URI, headers, status code, and response body.
- **Log4cats:**
    - Log4cats provides a type-safe logging API for Cats Effect.
    - You can use Log4cats to log messages from your middleware.

## 🛠️ Implementation: Logging Middleware

Here's how you can implement logging middleware for your http4s application:

Scala

```scala
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory
import com.comcast.ip4s._
import org.http4s.server.middleware.Logger

object Exercise1 extends IOApp {
  implicit val logging: LoggerFactory[IO] = Slf4jFactory.create[IO]

  private val httpRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")

    case req @ POST -> Root / "echo" =>
      req.as[String].flatMap(body => Ok(body))
  }

  private val loggedRoutes = Logger.httpApp(logHeaders = true, logBody = true)(httpRoutes.orNotFound)

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(loggedRoutes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
```

**Key Changes:**

- **`org.http4s.server.middleware.Logger`:**
    - This middleware is used to log requests and responses.
- **`Logger.httpApp(logHeaders = true, logBody = true)(httpRoutes.orNotFound)`:**
    - This applies the logging middleware to your `httpRoutes`.
    - `logHeaders = true` logs request and response headers.
    - `logBody = true` logs request and response bodies.
- **`withHttpApp(loggedRoutes)`:**
    - The Ember server is configured to use the logged routes.

With these changes, your application will now log request and response details. If you don't want to log the body, simply set logBody to false.

This approach provides a clean and effective way to log HTTP traffic in your http4s application.