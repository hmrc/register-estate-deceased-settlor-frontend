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

package utils.print

import com.google.inject.Inject
import models.{Address, Name, UserAnswers}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.{Html, HtmlFormat}
import queries.Gettable
import viewmodels.AnswerRow

import java.time.LocalDate

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, name: String)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name)

  class Bound(userAnswers: UserAnswers, name: String)(implicit messages: Messages) {

    def nameQuestion(query: Gettable[Name],
                     labelKey: String,
                     changeUrl: Option[String]): Option[AnswerRow] = {
      val format = (x: Name) => HtmlFormat.escape(x.displayFullName)
      question(query, labelKey, format, changeUrl)
    }

    def stringQuestion(query: Gettable[String],
                       labelKey: String,
                       changeUrl: Option[String]): Option[AnswerRow] = {
      question(query, labelKey, HtmlFormat.escape, changeUrl)
    }

    def yesNoQuestion(query: Gettable[Boolean],
                     labelKey: String,
                     changeUrl: Option[String]): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.yesOrNo, changeUrl)
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: Option[String]): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatDate, changeUrl)
    }

    def ninoQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: Option[String]): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatNino, changeUrl)
    }

    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: Option[String])
                                     (implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {
      question(query, labelKey, checkAnswersFormatters.formatAddress, changeUrl)
    }

    private def question[T](query: Gettable[T],
                            labelKey: String,
                            format: T => Html,
                            changeUrl: Option[String])
                           (implicit rds: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          label = messages(s"$labelKey.checkYourAnswersLabel", name),
          answer = format(x),
          changeUrl = changeUrl
        )
      }
    }

  }
}
