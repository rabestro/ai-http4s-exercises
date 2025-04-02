Certainly! Let's illustrate JSON handling in http4s with some sample code.

## 📖 Code Samples: JSON Handling in http4s

**1. Model Definition:**

Scala

```
import io.circe.generic.JsonCodec

@JsonCodec case class User(id: UUID, name: String, age: Int)
```

- `@JsonCodec` is a Circe annotation that automatically derives encoders and decoders for the `User` case class.

**2. http4s Routes:**

Scala

```
import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._
import org.http4s.implicits._
import java.util.UUID

object JsonRoutes {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "users" =>
      req.as[User].flatMap { user =>
        Ok(user)
      }

    case GET -> Root / "users" / UUIDVar(userId) =>
      val user = User(userId, "John Doe", 30)
      Ok(user)
  }
}
```

- `io.circe.generic.auto._` imports implicit encoders and decoders derived by Circe.
- `org.http4s.circe.CirceEntityCodec._` imports http4s's Circe integration.
- The POST route uses `req.as[User]` to decode the request body as a `User` object.
- The GET route returns a `User` object, which is automatically encoded as JSON.

**3. Example with manual encoder/decoder:**

Scala

```
import cats.effect.IO
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io._
import org.http4s.implicits._
import java.util.UUID

object ManualJsonRoutes {
  implicit val userDecoder: Decoder[User] = Decoder.forProduct3("user_id", "full_name", "years")(User.apply)
  implicit val userEncoder: Encoder[User] = Encoder.forProduct3("user_id", "full_name", "years")(u => (u.id, u.name, u.age))

  implicit val userEntityDecoder: EntityDecoder[IO, User] = jsonOf[IO, User]
  implicit val userEntityEncoder: EntityEncoder[IO, User] = jsonEncoderOf[IO, User]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "users" =>
      req.as[User].flatMap { user =>
        Ok(user)
      }

    case GET -> Root / "users" / UUIDVar(userId) =>
      val user = User(userId, "John Doe", 30)
      Ok(user)
  }
}
```

- The example shows how to create manual Encoders and Decoders.
- The Encoders and Decoders are then used to create EntityEncoders and EntityDecoders.
- The manual Encoders and Decoders allow to change the json field names.

These examples demonstrate how to handle JSON requests and responses in http4s using Circe. Do you have any questions about these examples?