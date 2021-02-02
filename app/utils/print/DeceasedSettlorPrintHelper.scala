/*
 * Copyright 2021 HM Revenue & Customs
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

package utils.print

import com.google.inject.Inject
import models.UserAnswers
import pages._
import play.api.i18n.Messages
import utils.countryOptions.AllCountryOptions
import viewmodels.AnswerSection

class DeceasedSettlorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                           countryOptions: AllCountryOptions
                                            ) {

  def apply(userAnswers: UserAnswers,
            settlorName: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, settlorName, countryOptions)

    AnswerSection(
      None,
      Seq(
        bound.nameQuestion(NamePage, "deceasedSettlor.name", Some(controllers.routes.NameController.onPageLoad().url)),
        bound.dateQuestion(DateOfDeathPage, "deceasedSettlor.dateOfDeath", Some(controllers.routes.DateOfDeathController.onPageLoad().url)),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "deceasedSettlor.dateOfBirthYesNo", Some(controllers.routes.DateOfBirthYesNoController.onPageLoad().url)),
        bound.dateQuestion(DateOfBirthPage, "deceasedSettlor.dateOfBirth",Some(controllers.routes.DateOfBirthController.onPageLoad().url)),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "deceasedSettlor.nationalInsuranceNumberYesNo", Some(controllers.routes.NationalInsuranceNumberYesNoController.onPageLoad().url)),
        bound.ninoQuestion(NationalInsuranceNumberPage, "deceasedSettlor.nationalInsuranceNumber", Some(controllers.routes.NationalInsuranceNumberController.onPageLoad().url)),
        bound.yesNoQuestion(AddressYesNoPage, "deceasedSettlor.addressYesNo", Some(controllers.routes.AddressYesNoController.onPageLoad().url)),
        bound.yesNoQuestion(LivedInTheUkYesNoPage, "deceasedSettlor.livedInTheUkYesNo", Some(controllers.routes.LivedInTheUkYesNoController.onPageLoad().url)),
        bound.addressQuestion(UkAddressPage, "deceasedSettlor.ukAddress", Some(controllers.routes.UkAddressController.onPageLoad().url)),
        bound.addressQuestion(NonUkAddressPage, "deceasedSettlor.nonUkAddress", Some(controllers.routes.NonUkAddressController.onPageLoad().url))
      ).flatten
    )
  }
}
