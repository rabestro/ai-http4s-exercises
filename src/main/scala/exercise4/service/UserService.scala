package exercise4.service

import cats.Monad
import cats.data.EitherT
import cats.effect.IO
import exercise4.error.{AppError, UserNotFound}
import exercise4.model.User

import java.util.UUID

class UserService[F[_]: Monad] {

  def getUser(userId: UUID): EitherT[F, AppError, User] = {
    // Simulate a database lookup
    if (userId == UUID.fromString("123e4567-e89b-12d3-a456-426614174000")) {
      EitherT.rightT(User(userId, "John Doe", 30))
    } else {
      EitherT.leftT(UserNotFound(userId))
    }
  }
}