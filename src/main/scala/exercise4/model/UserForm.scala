package exercise4.model

import io.circe.generic.JsonCodec


@JsonCodec
final case class UserForm(name: String, age: Int)
