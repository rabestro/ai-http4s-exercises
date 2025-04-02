package exercise4.model

import io.circe.generic.JsonCodec

/**
 * Represents the details of a user.
 *
 * @param name The name of the user.
 * @param age The age of the user.
 */
@JsonCodec
final case class UserDetails(name: String, age: Int)
