package forms

import org.scalatestplus.play.PlaySpec
import play.api.data.{Form, FormError}

trait FormSpec extends PlaySpec {
  def checkForError(form: Form[_], data: Map[String, String], expectedErrors: Seq[FormError]) = {
    form.bind(data).fold(
      formWithErrors => {
        for (error <- formWithErrors.errors) expectedErrors must contain(FormError(error.key, error.message))
        formWithErrors.errors.size mustBe expectedErrors.size
      },
      form => {
        fail("Expected a validation error when binding the form, but it was bound successfully.")
      }
    )
  }

  def error(key: String, value: String) = Seq(FormError(key, value))
}
