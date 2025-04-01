Excellent! Let's put your knowledge of path and query parameters to practice.

## 🏆 Hands-on Exercise 2: Path and Query Parameters in http4s

**Objective:** Create an http4s server that handles requests with path and query parameters.

**Instructions:**

1. **Create an `HttpRoutes`:**
    - Define a GET route at `/users/{userId}` that extracts the `userId` as a UUID.
    - Define a GET route at `/articles/{articleId}` that extracts the `articleId` as an integer.
    - Define a GET route at `/products` that extracts the following query parameters:
        - `category` (String, required)
        - `date` (LocalDate, required)
        - `page` (Int, optional, default to 1)
2. **Error Handling:**
    - If the `userId` is not a valid UUID, return a 400 Bad Request response.
    - If the `date` query parameter is not a valid LocalDate, return a 400 Bad Request response.
3. **Response:**
    - For the `/users/{userId}` and `/articles/{articleId}` routes, return a 200 OK response with a message containing the extracted parameter.
    - For the `/products` route, return a 200 OK response with a message containing the extracted query parameters.
4. **Start an Ember server:**
    - Use `EmberServerBuilder` to start a server on `localhost` port `8080` and mount your `HttpRoutes`.
5. **Test your endpoints:**
    - Use `curl` or a similar tool to send requests with various path and query parameters.
    - Verify that the responses are as expected.

**Example Requests:**

Bash

```
curl "localhost:8080/users/123e4567-e89b-12d3-a456-426614174000"
curl "localhost:8080/articles/42"
curl "localhost:8080/products?category=electronics&date=2025-04-01&page=2"
curl "localhost:8080/products?category=books&date=2025-04-01"
curl "localhost:8080/users/invalid-uuid"
curl "localhost:8080/products?category=books&date=invalid-date"
```

**Hints:**

- Use `org.http4s.dsl.io._` for convenient route matching.
- Use `java.util.UUID.fromString` to parse UUIDs.
- Use `java.time.LocalDate.parse` to parse LocalDate.
- Use `org.http4s.QueryParamDecoder` and `org.http4s.QueryParamDecoderMatcher` for query parameters.
- Use `cats.data.ValidatedNel` for error handling.
- Use `BadRequest(...)` to create a 400 Bad Request response.

Let me know how it goes or if you get stuck!