Great! Let's put your knowledge of HTTP Fundamentals to practice.

## 🏆 Hands-on Exercise 1: HTTP Basics with http4s

**Objective:** Create a simple http4s server that handles basic GET and POST requests.

**Instructions:**

1. **Set up:** If you haven't already, ensure you have http4s and Cats Effect in your project's dependencies. You can use sbt for this.
2. **Create a simple `HttpRoutes`:**
    - Define a GET route at `/hello/{name}` that returns a greeting like "Hello, {name}!".
    - Define a POST route at `/echo` that reads the request body and returns it as the response.
3. **Start an Ember server:** Use `EmberServerBuilder` to start a server on `localhost` port `8080` and mount your `HttpRoutes`.
4. **Test your endpoints:**
    - Use `curl` or a similar tool to send GET and POST requests to your server.
    - Verify that the responses are as expected.

**Example GET request:**

Bash

```
curl "localhost:8080/hello/Scala"
```

**Example POST request:**

Bash

```
curl -X POST "localhost:8080/echo" -d "This is a test"
```

**Hints:**

- Use `org.http4s.dsl.io._` for convenient route matching.
- Use `req.as[String]` to read the request body as a string.
- Use `Ok(...)` to create a successful response.

Let me know how it goes or if you get stuck!