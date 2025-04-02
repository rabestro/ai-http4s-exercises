Alright, let's break down how to approach the first exercise step-by-step.

## 🏆 Hands-on Exercise 1: HTTP Basics with http4s - Solution Guide

**1. Project Setup:**

- Ensure you have a Scala project set up with sbt.
- Add the necessary dependencies to your `build.sbt` file, as I provided earlier.
- Create a Scala file (e.g., `HttpBasics.scala`) in your `src/main/scala` directory.

**2. Import Statements:**

- Start by importing the necessary http4s and Cats Effect classes:

Scala

```
import cats.effect.{IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.comcast.ip4s._
```

**3. Define the `HttpRoutes`:**

- Use `HttpRoutes.of[IO]` to create a route definition.
- Define a GET route using `case GET -> Root / "hello" / name =>`.
    - Extract the `name` parameter from the URL path.
    - Return a `Response[IO]` with a greeting message using `Ok(s"Hello, $name!")`.
- Define a POST route using `case req @ POST -> Root / "echo" =>`.
    - Read the request body as a string using `req.as[String]`.
    - Return a `Response[IO]` with the request body using `Ok(...)`.

Scala

```scala
val httpRoutes = HttpRoutes.of[IO] {
  case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name!")

  case req @ POST -> Root / "echo" =>
    req.as[String].flatMap(body => Ok(body))
}
```

**4. Start the Ember Server:**

- Use `EmberServerBuilder.default[IO]` to create a server builder.
- Configure the host and port using `.withHost(ipv4"127.0.0.1")` and `.withPort(port"8080")`.
- Mount your `httpRoutes` using `.withHttpApp(httpRoutes.orNotFound)`.
- Use `.build.useForever` to start the server and keep it running.

Scala

```scala
object HttpBasics extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"8080")
      .withHttpApp(httpRoutes.orNotFound)
      .build
      .useForever
      .as(ExitCode.Success)
}
```

**5. Test the Endpoints:**

- **GET request:**
    - Open a terminal and run: `curl "localhost:8080/hello/Scala"`
    - You should see the response: `Hello, Scala!`
- **POST request:**
    - Run: `curl -X POST "localhost:8080/echo" -d "This is a test"`
    - You should see the response: `This is a test`

**Important Notes:**

- `orNotFound` is used to handle requests that don't match any defined routes.
- `flatMap` is used to chain `IO` actions together.
- `ExitCode.Success` indicates that the application ran successfully.

This detailed breakdown should help you implement the solution. Feel free to ask if you have any more questions!