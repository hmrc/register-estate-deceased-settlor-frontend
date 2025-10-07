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

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsResultException, Json}

import java.time.{LocalDate, LocalDateTime}

class MongoDateTimeFormatsSpec extends AnyFreeSpec with Matchers with OptionValues with MongoDateTimeFormats {

  "a LocalDateTime" - {

    val date = LocalDate.of(2018, 2, 1).atStartOfDay

    val dateMillis = 1517443200000L

    val json = Json.obj(
      s"$$date" -> dateMillis
    )

    "must serialise to json" in {
      val result = Json.toJson(date)
      result mustEqual json
    }

    "must deserialise from json" in {
      val result = json.as[LocalDateTime]
      result mustEqual date
    }

    "must serialise/deserialise to the same value" in {
      val result = Json.toJson(date).as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise when $date is an object containing $numberLong as a string" in {
      val jsonWithNumberLong = Json.obj(
        s"$$date" -> Json.obj(
          s"$$numberLong" -> dateMillis.toString
        )
      )

      val result = jsonWithNumberLong.as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise when $date is an ISO-8601 string with trailing Z (ZonedDateTime)" in {
      val jsonWithZString = Json.obj(
        s"$$date" -> "2018-02-01T00:00:00Z"
      )

      val result = jsonWithZString.as[LocalDateTime]
      result mustEqual date
    }

    "must deserialise when $date is an ISO-8601 string without Z (LocalDateTime)" in {
      val jsonWithoutZString = Json.obj(
        s"$$date" -> "2018-02-01T00:00:00"
      )

      val result = jsonWithoutZString.as[LocalDateTime]
      result mustEqual date
    }

    "must fail when $date is an unexpected object shape (missing $numberLong)" in {
      val badObject = Json.obj(
        s"$$date" -> Json.obj("notNumberLong" -> "something")
      )

      intercept[JsResultException] {
        badObject.as[LocalDateTime]
      }
    }

    "must fail when json does not contain $date at the top level" in {
      val noDateField = Json.obj("foo" -> "bar")

      intercept[JsResultException] {
        noDateField.as[LocalDateTime]
      }
    }
  }
}