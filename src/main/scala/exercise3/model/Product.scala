package exercise3.model

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
final case class Product(id: UUID, name: String, price: Double)
