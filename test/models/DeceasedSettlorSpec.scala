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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import java.time.LocalDate

class DeceasedSettlorSpec extends AnyWordSpec with Matchers {
  private val settlorWithNino = DeceasedSettlor(
    Name("First", None, "Last"),
    Some(LocalDate.of(1878, 4, 10)),
    Some(LocalDate.of(1960, 8, 2)),
    Some(NationalInsuranceNumber("Neenoh")),
    None,
    None)

  private val jsonWithNino = Json.parse(
    """
      |{
      | "name": {
      |   "firstName": "First",
      |   "lastName": "Last"
      | },
      | "dateOfBirth": "1878-04-10",
      | "dateOfDeath": "1960-08-02",
      | "identification": {
      |   "nino": "Neenoh"
      | }
      |}
      |""".stripMargin)

  private val settlorWithUkAddress = DeceasedSettlor(
    Name("First", None, "Last"),
    Some(LocalDate.of(1878, 4, 10)),
    Some(LocalDate.of(1960, 8, 2)),
    None,
    Some(true),
    Some(UkAddress("Line1", "Line2", None, None, "PostCode")))

  private val jsonWithUkAddress = Json.parse(
    """
      |{
      | "name": {
      |   "firstName": "First",
      |   "lastName": "Last"
      | },
      | "dateOfBirth": "1878-04-10",
      | "dateOfDeath": "1960-08-02",
      | "addressYesNo": true,
      | "identification": {
      |   "address": {
      |     "line1": "Line1",
      |     "line2": "Line2",
      |     "postCode": "PostCode",
      |     "country": "GB"
      |   }
      | }
      |}
      |""".stripMargin)


  ".reads" must {
    "read fields" in {
      jsonWithNino.validate[DeceasedSettlor].get mustBe settlorWithNino
      jsonWithUkAddress.validate[DeceasedSettlor].get mustBe settlorWithUkAddress
    }
  }

  ".writes" must {

    "write fields" in {
      Json.toJson(settlorWithNino) mustBe jsonWithNino
      Json.toJson(settlorWithUkAddress) mustBe jsonWithUkAddress
    }

    "not write an empty identification" in {
      val settlor = DeceasedSettlor(Name("First", None, "Last"), None, None, None, None, None)

      val json = Json.toJson(settlor)
      json mustBe Json.parse(
        """
          |{
          | "name": {
          |   "firstName": "First",
          |   "lastName": "Last"
          | }
          |}
          |""".stripMargin)
    }
  }
}
