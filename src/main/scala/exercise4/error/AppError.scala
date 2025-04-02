package exercise4.error

import cats.data.NonEmptyList

import java.util.UUID

sealed trait AppError

case class UserNotFound(userId: UUID) extends AppError

case class InvalidUserData(errors: NonEmptyList[String]) extends AppError