Excellent! Let's put your knowledge of JSON handling to practice.

## 🏆 Hands-on Exercise 3: JSON Handling in http4s

**Objective:** Create an http4s server that handles JSON requests and responses.

**Instructions:**

1. **Define a Case Class:**
    - Create a case class named `Product` with fields `id` (UUID), `name` (String), and `price` (Double).
    - Use Circe's `@JsonCodec` annotation to automatically derive encoders and decoders.
2. **Create an `HttpRoutes`:**
    - Define a POST route at `/products` that accepts a JSON request body representing a `Product`.
    - Return a 201 Created response with the created `Product` as JSON.
    - Define a GET route at `/products/{productId}` that returns a `Product` as JSON.
3. **Error Handling:**
    - If the request body for the POST route is invalid JSON or cannot be decoded as a `Product`, return a 400 Bad Request response.
    - If the `productId` in the GET route is not a valid UUID, return a 400 Bad Request response.
4. **Start an Ember server:**
    - Use `EmberServerBuilder` to start a server on `localhost` port `8080` and mount your `HttpRoutes`.
5. **Test your endpoints:**
    - Use `curl` or a similar tool to send JSON requests to your server.
    - Verify that the responses are as expected.

**Example Requests:**

Bash

```
curl -X POST -H "Content-Type: application/json" -d '{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Laptop",
  "price": 1200.00
}' "localhost:8080/products"

curl "localhost:8080/products/123e4567-e89b-12d3-a456-426614174000"

curl -X POST -H "Content-Type: application/json" -d '{
  "id": "invalid-uuid",
  "name": "Laptop",
  "price": 1200.00
}' "localhost:8080/products"
```

**Hints:**

- Use `io.circe.generic.auto._` and `org.http4s.circe.CirceEntityCodec._` for JSON handling.
- Use `Created(...)` to create a 201 Created response.
- Use `BadRequest(...)` to create a 400 Bad Request response.
- Use `java.util.UUID.fromString` to parse UUIDs.
- Use `Either.catchOnly[IllegalArgumentException]` for exception handling.