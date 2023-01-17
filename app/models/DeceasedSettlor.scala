/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class DeceasedSettlor(name: Name,
                                 dateOfBirth: Option[LocalDate],
                                 dateOfDeath: Option[LocalDate],
                                 nino: Option[NationalInsuranceNumber],
                                 address : Option[Address])

object DeceasedSettlor {

  implicit val reads: Reads[DeceasedSettlor] =
    ((__ \ 'name).read[Name] and
      (__ \ 'dateOfBirth).readNullable[LocalDate] and
      (__ \ 'dateOfDeath).readNullable[LocalDate] and
      __.lazyRead(readNullableAtSubPath[NationalInsuranceNumber](__ \ 'identification)) and
      __.lazyRead(readNullableAtSubPath[Address](__ \ 'identification \ 'address))).tupled.map{

      case (name, dob, dod, nino, identification) =>
        DeceasedSettlor(name, dob, dod, nino, identification)

    }

  implicit val writes: Writes[DeceasedSettlor] =
    ((__ \ 'name).write[Name] and
      (__ \ 'dateOfBirth).writeNullable[LocalDate] and
      (__ \ 'dateOfDeath).writeNullable[LocalDate] and
      (__ \ 'identification).writeNullable[NationalInsuranceNumber] and
      (__ \ 'identification \ 'address).writeNullable[Address]
      ).apply(settlor => (
      settlor.name,
      settlor.dateOfBirth,
      settlor.dateOfDeath,
      settlor.nino,
      settlor.address
    ))

  def readNullableAtSubPath[T:Reads](subPath : JsPath) : Reads[Option[T]] = Reads (
    _.transform(subPath.json.pick)
      .flatMap(_.validate[T])
      .map(Some(_))
      .recoverWith(_ => JsSuccess(None))
  )
}
