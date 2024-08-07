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

package views

import controllers.routes
import forms.NonUkAddressFormProvider
import models.{Name, NonUkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.NonUkAddressViewBehaviours
import views.html.NonUkAddressView

class NonUkAddressViewSpec extends NonUkAddressViewBehaviours {

  val messageKeyPrefix = "deceasedSettlor.nonUkAddress"
  val name: Name = Name("First", Some("Middle"), "Last")

  override val form: Form[NonUkAddress] = new NonUkAddressFormProvider().apply()

  "NonUkAddressView" must {

    val view = viewFor[NonUkAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options()

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, name.displayName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.displayName)

    behave like pageWithBackLink(applyView(form))

    behave like nonUkAddressPage(
      applyView,
      Some(messageKeyPrefix),
      routes.NonUkAddressController.onSubmit().url,
      name.displayName
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
