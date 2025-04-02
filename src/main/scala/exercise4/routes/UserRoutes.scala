package exercise4.routes

import cats.effect.IO
import exercise3.model.Product
import exercise4.service.UserService
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

import java.util.UUID
import scala.util.Try

class UserRoutes(userService: UserService[IO]) {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      IO.fromEither(Try(UUID.fromString(userId)).toEither)
        .flatMap(userId => Ok(s"User ID: $userId"))
        .handleErrorWith(_ => BadRequest("Invalid UUID format"))

    case req@POST -> Root / "products" =>
      req.as[Product].flatMap { product =>
        Created(product)
      }.handleErrorWith { error =>
        BadRequest(Json.obj("error" -> Json.fromString("Failed to parse product request")))
      }
    case GET -> Root / "products" / productId =>
      IO.fromEither(Try(UUID.fromString(productId)).toEither)
        .flatMap(id => Ok(Product(id, "Some Product", 30.5)))
        .handleErrorWith(_ => BadRequest(Json.obj("error" -> Json.fromString("Invalid UUID format"))))
  }
}