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

package utils.extractors

import java.time.LocalDate

import models._
import pages._

import scala.util.Try

class DeceasedExtractor {

  def apply(deceased: DeceasedSettlor, userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.set(NamePage, deceased.name)
      .flatMap(_.set(DateOfDeathPage, deceased.dateOfDeath.get))
      .flatMap(answers => extractDateOfBirth(deceased.dateOfBirth, answers))
      .flatMap(answers => extractIdentification(deceased, answers))
  }

  private def extractDateOfBirth(dateOfBirth: Option[LocalDate], userAnswers: UserAnswers): Try[UserAnswers] = {
    dateOfBirth match {
      case Some(dateOfBirth) =>
        userAnswers.set(DateOfBirthYesNoPage, true)
          .flatMap(_.set(DateOfBirthPage, dateOfBirth))

      case None =>
        userAnswers.set(DateOfBirthYesNoPage, false)
    }
  }

  private def extractIdentification(deceased: DeceasedSettlor, userAnswers: UserAnswers) = {
    (deceased.nino, deceased.address) match {
      case (Some(nino), _) =>
        userAnswers.set(NationalInsuranceNumberYesNoPage, true)
          .flatMap(_.set(NationalInsuranceNumberPage, nino.nino))

      case (None, Some(address: UkAddress)) =>
        userAnswers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(AddressYesNoPage, true))
          .flatMap(_.set(LivedInTheUkYesNoPage, true))
          .flatMap(_.set(UkAddressPage, address))

      case (None, Some(address: NonUkAddress)) =>
        userAnswers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(AddressYesNoPage, true))
          .flatMap(_.set(LivedInTheUkYesNoPage, false))
          .flatMap(_.set(NonUkAddressPage, address))

      case (_, _) =>
        userAnswers.set(NationalInsuranceNumberYesNoPage, false)
          .flatMap(_.set(AddressYesNoPage, false))
    }
  }
}
