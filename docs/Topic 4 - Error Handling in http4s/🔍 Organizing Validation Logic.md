Ah, a very important question! As Scala Sage, I'm delighted to guide you through the best practices for organizing your validation logic.

## 📖 Theoretical Explanation: Organizing Validation Logic

Validation logic is a critical part of any application, and its organization can significantly impact code maintainability and testability. Here are the best practices:

**1. Validation in Model Classes (Limited):**

- **Simple Constraints:** For simple constraints directly related to the model's data, you can include validation logic within the model class itself.
- **Example:**
    
    Scala
    
    ```
    case class User(name: String, age: Int) {
      require(name.nonEmpty, "Name cannot be empty")
      require(age > 0, "Age must be positive")
    }
    ```
    
- **Limitations:**
    - This approach is suitable for simple constraints but can clutter the model class for complex validation.
    - It makes it harder to reuse validation logic across different contexts.

**2. Validation in Dedicated Validator Classes/Objects (Recommended):**

- **Separation of Concerns:** Create dedicated validator classes or objects to encapsulate validation logic.
- **Reusability:** This approach promotes code reuse across different parts of your application.
- **Testability:** Validator classes are easier to test in isolation.
- **Example:**
    
    Scala
    
    ```
    object UserValidator {
      def validateName(name: String): ValidatedNel[String, String] =
        if (name.nonEmpty) name.validNel else "Name cannot be empty".invalidNel
    
      def validateAge(age: Int): ValidatedNel[String, Int] =
        if (age > 0) age.validNel else "Age must be positive".invalidNel
    
      def validateUser(user: User): ValidatedNel[String, User] =
        (validateName(user.name), validateAge(user.age)).mapN((_, _) => user)
    }
    ```
    
- **Benefits:**
    - Clean separation of concerns.
    - Improved reusability and testability.
    - Easier to manage complex validation rules.

**3. Validation in Service Layer (Context-Specific):**

- **Business Logic Validation:** For validation that depends on business logic or external resources, place the validation logic in the service layer.
- **Example:**
    
    Scala
    
    ```
    class UserService[F[_]: Monad](userRepository: UserRepository[F]) {
      def createUser(user: User): F[User] = {
        if (userRepository.userExists(user.name)) {
          Monad[F].raiseError(new IllegalArgumentException("User with this name already exists"))
        } else {
          userRepository.createUser(user)
        }
      }
    }
    ```
    
- **Benefits:**
    - Encapsulates validation logic that depends on business logic.
    - Keeps the service layer cohesive.

**4. Validation in Form Objects (Web Applications):**

- **Request Validation:** For web applications, create form objects to represent request data and perform validation on these objects.
- **http4s Example:**
    
    Scala
    
    ```
    case class UserForm(name: String, age: String)
    
    object UserFormValidator {
      def validateUserForm(form: UserForm): ValidatedNel[String, User] = {
        // ... validation logic ...
      }
    }
    ```
    
- **Benefits:**
    - Isolates request-specific validation logic.
    - Improves code organization in web applications.

**Recommendation:**

- **Use dedicated validator classes/objects** for most validation logic.
- **Place business logic validation in the service layer.**
- **Use form objects for request validation in web applications.**
- **Limit validation in model classes to simple constraints.**

By following these best practices, you can create a well-organized and maintainable validation layer in your Scala applications.