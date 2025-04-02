package exercise4.model

import io.circe.generic.JsonCodec

import java.util.UUID

/**
 * Represents a user in the system.
 *
 * @param id The unique identifier of the user.
 * @param name The name of the user.
 * @param age The age of the user.
 */
@JsonCodec
final case class User(id: UUID, name: String, age: Int)
