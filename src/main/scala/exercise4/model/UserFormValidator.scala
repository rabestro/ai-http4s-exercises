package exercise4.model

import cats.data.ValidatedNel
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedId}

object UserFormValidator {

  def validateUserForm(form: UserForm): ValidatedNel[String, UserForm] = {
    val nameValidation =
      if (form.name.nonEmpty) form.name.validNel
      else "Name cannot be empty".invalidNel

    val ageValidation =
      if (form.age > 0) form.age.validNel
      else "Age must be positive".invalidNel

    (nameValidation, ageValidation).mapN(UserForm.apply)
  }
}
