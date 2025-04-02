
> [!QUESTION] Where do you recommend putting methods like getUser?


Ah, an excellent question! As Scala Sage, I recommend placing methods like `getUser` in a dedicated service layer, separate from your routes and error definitions.

## 📖 Theoretical Explanation: Service Layer for Business Logic

- **Separation of Concerns:**
    - Routes should handle HTTP-specific logic (e.g., request parsing, response generation).1
    - Services should handle business logic (e.g., data retrieval, validation).
    - This separation promotes modularity and maintainability.
- **Reusability:**
    - Services can be reused across multiple routes or even in non-HTTP contexts.
    - This promotes code reuse and reduces duplication.
- **Testability:**
    - Services are easier to test in isolation, without the complexities of HTTP.
    - This leads to more robust and reliable code.
- **Maintainability:**
    - Keeping business logic separate makes it easier to modify and extend.2
    - Changes to the business logic don't affect the routes, and vice versa.

**Recommendation:**

- Create a service class or object (e.g., `UserService`) to encapsulate your business logic.
- Place the service in a dedicated package (e.g., `com.example.myapp.service`).
- Inject the service into your routes using constructor injection.

**Example Package Structure:**

```
src/
  main/
    scala/
      com/
        example/
          myapp/
            error/
              AppError.scala
            model/
              User.scala
            service/
              UserService.scala
            route/
              UserRoutes.scala
```

**Example `UserService.scala`:**

Scala

```scala
package com.example.myapp.service

import cats.data.EitherT
import cats.effect.IO
import com.example.myapp.error.{AppError, UserNotFound}
import com.example.myapp.model.User
import java.util.UUID

class UserService {

  def getUser(userId: UUID): EitherT[IO, AppError, User] = {
    // Simulate a database lookup
    if (userId == UUID.fromString("123e4567-e89b-12d3-a456-426614174000")) {
      EitherT.rightT[IO, AppError, User](User(userId, "John Doe", 30))
    } else {
      EitherT.leftT[IO, User](UserNotFound(userId))
    }
  }
}
```

**Example `UserRoutes.scala` (with dependency injection):**

Scala

```scala
package com.example.myapp.route

import cats.effect.IO
import com.example.myapp.service.UserService
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import java.util.UUID

class UserRoutes(userService: UserService) {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / userId =>
      IO.fromEither(Either.catchOnly[IllegalArgumentException](UUID.fromString(userId)))
        .flatMap { id =>
          userService.getUser(id).value.flatMap {
            case Right(user) => Ok(user)
            case Left(error) => error match {
              case _ : com.example.myapp.error.UserNotFound => NotFound("User not found")
              case _ => InternalServerError("internal server error")
            }
          }
        }
        .handleErrorWith(_ => BadRequest("Invalid UUID format"))
  }
}
```

By adhering to this pattern, your code will be more structured, maintainable, and testable.