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

package utils.mappers

import java.time.LocalDate

import models.{Address, DeceasedSettlor, Name, NationalInsuranceNumber, NonUkAddress, UkAddress, UserAnswers}
import pages._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads}

class DeceasedSettlorMapper extends Logging {

  def apply(answers: UserAnswers): Option[DeceasedSettlor] = {
    val readFromUserAnswers: Reads[DeceasedSettlor] =
      (
        NamePage.path.read[Name] and
        DateOfBirthPage.path.readNullable[LocalDate] and
        DateOfDeathPage.path.readNullable[LocalDate] and
        readNationalInsuranceNumber and
        readAddress
      ) (DeceasedSettlor.apply _)

    answers.data.validate[DeceasedSettlor](readFromUserAnswers) match {
      case JsSuccess(value, _) =>
        Some(value)
      case JsError(errors) =>
        logger.error(s"Failed to rehydrate DeceasedSettlor from UserAnswers due to $errors")
        None
    }
  }

  private def readNationalInsuranceNumber: Reads[Option[NationalInsuranceNumber]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap[Option[NationalInsuranceNumber]] {
      case true => NationalInsuranceNumberPage.path.read[String].map(nino => Some(NationalInsuranceNumber(nino)))
      case false => Reads(_ => JsSuccess(None))
    }
  }

  private def readAddress: Reads[Option[Address]] = {
    NationalInsuranceNumberYesNoPage.path.read[Boolean].flatMap {
      case true => Reads(_ => JsSuccess(None))
      case false => AddressYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
        case true => readUkOrNonUkAddress
        case false => Reads(_ => JsSuccess(None))
      }
    }
  }

  private def readUkOrNonUkAddress: Reads[Option[Address]] = {
    LivedInTheUkYesNoPage.path.read[Boolean].flatMap[Option[Address]] {
      case true => UkAddressPage.path.read[UkAddress].map(Some(_))
      case false => NonUkAddressPage.path.read[NonUkAddress].map(Some(_))
    }
  }

}
