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

import base.SpecBase
import models._
import pages._

import java.time.LocalDate

class DeceasedExtractorSpec extends SpecBase {

  private val name = Name("John", None, "Doe")
  private val nino = "AA000000A"
  private val dateOfBirth = LocalDate.parse("1968-05-12")
  private val dateOfDeath = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val country: String = "country"
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), country)

  "Deceased Extractor" when {

    val extractor = injector.instanceOf[DeceasedExtractor]

    "extract answers for deceased with bare minimum" in {

      val deceased = DeceasedSettlor(
        name = name,
        dateOfBirth = None,
        dateOfDeath = Some(dateOfDeath),
        nino = None,
        address = None
      )

      val result = extractor(deceased, emptyUserAnswers).success.value

      result.get(NamePage) mustBe Some(name)
      result.get(DateOfDeathPage) mustBe Some(dateOfDeath)
      result.get(DateOfBirthYesNoPage) mustBe Some(false)
      result.get(NationalInsuranceNumberYesNoPage) mustBe Some(false)
      result.get(NationalInsuranceNumberPage) mustBe None
      result.get(AddressYesNoPage) mustBe Some(false)
      result.get(LivedInTheUkYesNoPage) mustBe None
      result.get(UkAddressPage) mustBe None
      result.get(NonUkAddressPage) mustBe None
    }

    "extract answers for deceased with date of birth" in {

      val deceased = DeceasedSettlor(
        name = name,
        dateOfBirth = Some(dateOfBirth),
        dateOfDeath = Some(dateOfDeath),
        nino = None,
        address = None
      )

      val result = extractor(deceased, emptyUserAnswers).success.value

      result.get(DateOfBirthYesNoPage) mustBe Some(true)
      result.get(DateOfBirthPage) mustBe Some(dateOfBirth)
    }

    "extract answers for deceased with nino" in {

      val deceased = DeceasedSettlor(
        name = name,
        dateOfBirth = None,
        dateOfDeath = Some(dateOfDeath),
        nino = Some(NationalInsuranceNumber(nino)),
        address = None
      )

      val result = extractor(deceased, emptyUserAnswers).success.value

      result.get(NationalInsuranceNumberYesNoPage) mustBe Some(true)
      result.get(NationalInsuranceNumberPage) mustBe Some(nino)
      result.get(AddressYesNoPage) mustBe None
      result.get(LivedInTheUkYesNoPage) mustBe None
      result.get(UkAddressPage) mustBe None
      result.get(NonUkAddressPage) mustBe None
    }

    "extract answers for deceased with UK address" in {

      val deceased = DeceasedSettlor(
        name = name,
        dateOfBirth = None,
        dateOfDeath = Some(dateOfDeath),
        nino = None,
        address = Some(ukAddress)
      )

      val result = extractor(deceased, emptyUserAnswers).success.value

      result.get(NationalInsuranceNumberYesNoPage) mustBe Some(false)
      result.get(NationalInsuranceNumberPage) mustBe None
      result.get(AddressYesNoPage) mustBe Some(true)
      result.get(LivedInTheUkYesNoPage) mustBe Some(true)
      result.get(UkAddressPage) mustBe Some(ukAddress)
      result.get(NonUkAddressPage) mustBe None
    }

    "extract answers for deceased with non-UK address" in {

      val deceased = DeceasedSettlor(
        name = name,
        dateOfBirth = None,
        dateOfDeath = Some(dateOfDeath),
        nino = None,
        address = Some(nonUkAddress)
      )

      val result = extractor(deceased, emptyUserAnswers).success.value

      result.get(NationalInsuranceNumberYesNoPage) mustBe Some(false)
      result.get(NationalInsuranceNumberPage) mustBe None
      result.get(AddressYesNoPage) mustBe Some(true)
      result.get(LivedInTheUkYesNoPage) mustBe Some(false)
      result.get(UkAddressPage) mustBe None
      result.get(NonUkAddressPage) mustBe Some(nonUkAddress)
    }

  }

}
