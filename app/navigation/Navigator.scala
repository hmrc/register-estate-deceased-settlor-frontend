/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    navRoute(page)(userAnswers)

  private val defaultRoute: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private lazy val checkYourAnswersRoute: Call = routes.CheckDetailsController.onPageLoad()

  private val normalRoute: PartialFunction[Page, UserAnswers => Call] = {
    case NamePage => _ => routes.DateOfDeathController.onPageLoad()
    case DateOfDeathPage => _ => routes.DateOfBirthYesNoController.onPageLoad()
    case DateOfBirthPage => _ => routes.NationalInsuranceNumberYesNoController.onPageLoad()
    case NationalInsuranceNumberPage => _ => checkYourAnswersRoute
    case UkAddressPage => _ => checkYourAnswersRoute
    case NonUkAddressPage => _ => checkYourAnswersRoute
  }

  private val yesNoRoute: PartialFunction[Page, UserAnswers => Call] = {
    case DateOfBirthYesNoPage => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage, routes.DateOfBirthController.onPageLoad(), routes.NationalInsuranceNumberYesNoController.onPageLoad())
    case NationalInsuranceNumberYesNoPage => ua =>
      yesNoNav(ua, NationalInsuranceNumberYesNoPage, routes.NationalInsuranceNumberController.onPageLoad(), routes.AddressYesNoController.onPageLoad())
    case AddressYesNoPage => ua =>
      yesNoNav(ua, AddressYesNoPage, routes.LivedInTheUkYesNoController.onPageLoad(), checkYourAnswersRoute)
    case LivedInTheUkYesNoPage => ua =>
      yesNoNav(ua, LivedInTheUkYesNoPage, routes.UkAddressController.onPageLoad(), routes.NonUkAddressController.onPageLoad())
  }

  private val navRoute: PartialFunction[Page, UserAnswers => Call] =
    normalRoute orElse
      yesNoRoute orElse
      defaultRoute

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
