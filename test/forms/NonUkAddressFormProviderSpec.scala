/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import forms.behaviours.StringFieldBehaviours
import models.NonUkAddress
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class NonUkAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new NonUkAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = "nonUkAddress.error.line1.required"
    val lengthKey = "nonUkAddress.error.line1.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = "nonUkAddress.error.line2.required"
    val lengthKey = "nonUkAddress.error.line2.length"
    val maxLength = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.addressLineRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".line3" must {

    val fieldName = "line3"
    val lengthKey = "nonUkAddress.error.line3.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.addressLineRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  line3  ", "country" -> "country"))
      result.value.value.line3 shouldBe Some("line3")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "  ", "country" -> "country"))
      result.value.value.line3 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("line1" -> "line1", "line2" -> "line2", "line3" -> "", "country" -> "country"))
      result.value.value.line3 shouldBe None
    }
  }

  ".country" must {

    val fieldName = "country"
    val requiredKey = "nonUkAddress.error.country.required"
    val lengthKey = "nonUkAddress.error.country.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  "address lines" must {
    "bind whitespace, trim text, and replace smart apostrophes with single quotes" in {
      val addressLine = s"‘AddressLine’  "
      val result = form.bind(
        Map("line1" -> addressLine, "line2" -> addressLine, "line3" -> addressLine, "country" -> "England")
      )

      result.value.value shouldBe NonUkAddress("'AddressLine'", "'AddressLine'", Some("'AddressLine'"), "England")
    }
  }
}
