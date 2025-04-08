package exercise4.model

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class UserFormValidatorSpec extends AnyFlatSpec {

  "UserFormValidator" should "validate a valid UserForm" in {
    val form = UserForm("John Doe", 30)
    val result = UserFormValidator.validateUserForm(form)

    result shouldBe Valid(form)
  }

  it should "invalidate a UserForm with empty name" in {
    val form = UserForm("", 30)
    val result = UserFormValidator.validateUserForm(form)

    result shouldBe Invalid(NonEmptyList.one("Name cannot be empty"))
  }

  it should "invalidate a UserForm with non-positive age" in {
    val form = UserForm("John Doe", 0)
    val result = UserFormValidator.validateUserForm(form)

    result shouldBe Invalid(NonEmptyList.one("Age must be positive"))
  }

  it should "invalidate a UserForm with negative age" in {
    val form = UserForm("John Doe", -1)
    val result = UserFormValidator.validateUserForm(form)

    result shouldBe Invalid(NonEmptyList.one("Age must be positive"))
  }

  it should "invalidate a UserForm with empty name and non-positive age" in {
    val form = UserForm("", 0)
    val result = UserFormValidator.validateUserForm(form)

    result shouldBe Invalid(NonEmptyList.of("Name cannot be empty", "Age must be positive"))
  }
}
