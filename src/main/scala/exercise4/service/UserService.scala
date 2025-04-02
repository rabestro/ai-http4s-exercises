package exercise4.service

import cats.Monad
import cats.data.EitherT
import exercise4.error.{AppError, UserNotFound}
import exercise4.model.{User, UserDetails}

import java.util.UUID

class UserService[F[_] : Monad] {

  /**
   * Retrieves a user by their ID.
   *
   * @param id The unique identifier of the user.
   * @return An effect that resolves to either a user or an error.
   */
  def getUser(id: UUID): EitherT[F, AppError, User] = {
    // Simulate a database lookup
    if (id == UUID.fromString("123e4567-e89b-12d3-a456-426614174000")) {
      EitherT.rightT(User(id, "John Doe", 30))
    } else {
      EitherT.leftT(UserNotFound(id))
    }
  }

  /**
   * Creates a new user.
   *
   * @param userDetails The details of the user to create.
   * @return An effect that resolves to the created user.
   */
  def createUser(userDetails: UserDetails): F[User] = {
    // Simulate an effectful database insert
    Monad[F].pure {
      // Perform database insert here
      User(UUID.randomUUID(), userDetails.name, userDetails.age)
    }
  }
}
