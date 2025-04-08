package exercise4.model

import io.circe.generic.JsonCodec

import java.util.UUID


@JsonCodec
final case class User(id: UUID, name: String, age: Int)
