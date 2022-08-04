/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation

import base.SpecBase
import controllers.routes
import pages._
import models._

class NavigatorSpec extends SpecBase {

  private val navigator = new Navigator
  private val baseAnswers = emptyUserAnswers

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
      }

      "Name page -> Date of death" in {

        navigator.nextPage(NamePage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.DateOfDeathController.onPageLoad())
      }

      "Date of death page -> Date of birth yes/no page" in {

        navigator.nextPage(DateOfDeathPage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.DateOfBirthYesNoController.onPageLoad())
      }

      "Do you know date of birth page -> Yes -> Date of birth page" in {
        val answers = baseAnswers
          .set(DateOfBirthYesNoPage, true).success.value

        navigator.nextPage(DateOfBirthYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.DateOfBirthController.onPageLoad())
      }

      "Date of birth page -> Do you know NINO page" in {
        navigator.nextPage(DateOfBirthPage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.NationalInsuranceNumberYesNoController.onPageLoad())
      }

      "Do you know date of birth page -> No -> Do you know NINO page" in {
        val answers = baseAnswers
          .set(DateOfBirthYesNoPage, false).success.value

        navigator.nextPage(DateOfBirthYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.NationalInsuranceNumberYesNoController.onPageLoad())
      }

      "Do you know NINO page -> Yes -> NINO page" in {
        val answers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, true).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.NationalInsuranceNumberController.onPageLoad())
      }

      "Do you know NINO page -> No -> Do you know address page" in {
        val answers = baseAnswers
          .set(NationalInsuranceNumberYesNoPage, false).success.value

        navigator.nextPage(NationalInsuranceNumberYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.AddressYesNoController.onPageLoad())
      }

      "NINO page -> Check details page" in {
        navigator.nextPage(NationalInsuranceNumberPage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.CheckDetailsController.onPageLoad())
      }

      "Do you know last address page -> Yes -> Lived in UK page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, true).success.value

        navigator.nextPage(AddressYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.LivedInTheUkYesNoController.onPageLoad())
      }

      "Do you know last address page -> No -> Check details page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage, false).success.value

        navigator.nextPage(AddressYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.CheckDetailsController.onPageLoad())
      }

      "Lived in the UK page -> Yes -> UK address page" in {
        val answers = baseAnswers
          .set(LivedInTheUkYesNoPage, true).success.value

        navigator.nextPage(LivedInTheUkYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.UkAddressController.onPageLoad())
      }

      "Lived in the UK page -> No -> Non-UK address page" in {
        val answers = baseAnswers
          .set(LivedInTheUkYesNoPage, false).success.value

        navigator.nextPage(LivedInTheUkYesNoPage, NormalMode, answers)
          .mustBe(controllers.routes.NonUkAddressController.onPageLoad())
      }

      "Uk Address page -> Check details page" in {
        navigator.nextPage(UkAddressPage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.CheckDetailsController.onPageLoad())
      }

      "Non-Uk Address page -> Check details page" in {
        navigator.nextPage(NonUkAddressPage, NormalMode, baseAnswers)
          .mustBe(controllers.routes.CheckDetailsController.onPageLoad())
      }
    }
  }
}
