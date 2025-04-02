Excellent! Let's put your knowledge of error handling to practice.

## 🏆 Hands-on Exercise 4: Error Handling in http4s

**Objective:** Create an http4s server that handles errors gracefully and provides meaningful responses to clients.

**Instructions:**

1. **Define Error Types:**
    - Create a sealed trait named `AppError` to represent application-specific errors.
    - Define case classes `UserNotFound(userId: UUID)` and `InvalidUserData(errors: NonEmptyList[String])` that extends `AppError`.
2. **Create an `HttpRoutes`:**
    - Define a GET route at `/users/{userId}` that retrieves a user by ID.
        - If the `userId` is not a valid UUID, return a 400 Bad Request response.
        - If the user is not found, return a 404 Not Found response with a JSON body containing the error message.
        - If the user is found, return a 200 OK response with the user data as JSON.
    - Define a POST route at `/users` that accepts user data (name and age) as JSON.
        - Validate the user data:
            - Name cannot be empty.
            - Age must be a positive integer.
        - If the user data is invalid, return a 400 Bad Request response with a JSON body containing the validation errors.
        - If the user data is valid, return a 201 Created response with the user data as JSON.
3. **Error Handling:**
    - Use `EitherT` to handle errors in the GET route.
    - Use `ValidatedNel` to handle validation errors in the POST route.
    - Use appropriate HTTP status codes and JSON error responses.
4. **Start an Ember server:**
    - Use `EmberServerBuilder` to start a server on `localhost` port `8080` and mount your `HttpRoutes`.
5. **Test your endpoints:**
    - Use `curl` or a similar tool to send requests with various inputs.
    - Verify that the responses are as expected.

**Example Requests:**

Bash

```
curl "localhost:8080/users/123e4567-e89b-12d3-a456-426614174000"
curl "localhost:8080/users/invalid-uuid"
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "John Doe",
  "age": 30
}' "localhost:8080/users"
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "",
  "age": -1
}' "localhost:8080/users"
```

**Hints:**

- Use `cats.data.EitherT` and `cats.data.ValidatedNel` for error handling.
- Use `io.circe.generic.auto._` and `org.http4s.circe.CirceEntityCodec._` for JSON handling.
- Use `org.http4s.dsl.io._` for convenient route matching.
- Use `java.util.UUID.fromString` to parse UUIDs.
- Use `cats.implicits._` for convenient syntax.
- Use `org.http4s.circe.Json` to create JSON error responses.
