package exercise3.model

import io.circe.generic.JsonCodec

import java.util.UUID

/**
 * Represents a product in the system.
 *
 * @param id The unique identifier for the product.
 * @param name The name of the product.
 * @param price The price of the product.
 */
@JsonCodec
final case class Product(id: UUID, name: String, price: Double)
