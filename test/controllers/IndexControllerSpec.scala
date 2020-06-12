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

package controllers

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.{DeceasedSettlor, Name, NationalInsuranceNumber, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockitoSugar {

  "Index Controller" must {

    "return redirect to name controller if no existing deceased data" in {
      val estatesConnector = mock[EstatesConnector]
      when(estatesConnector.getDeceased()(any(), any())).thenReturn(Future.successful(None))

      val sessionRepository = mock[SessionRepository]
      when(sessionRepository.set(any[UserAnswers])).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(estatesConnector))
        .overrides(bind[SessionRepository].toInstance(sessionRepository))
        .build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.NameController.onPageLoad().url

      application.stop()
    }
  }

  "return redirect to name controller if no existing deceased data" in {
    val deceased = DeceasedSettlor(
      Name("First", None, "Last"),
      Some(LocalDate.of(1972, 9, 18)),
      Some(LocalDate.of(2018, 2, 23)),
      Some(NationalInsuranceNumber("AA111111B")),
      None
    )

    val estatesConnector = mock[EstatesConnector]
    when(estatesConnector.getDeceased()(any(), any())).thenReturn(Future.successful(Some(deceased)))

    val sessionRepository = mock[SessionRepository]
    when(sessionRepository.set(any[UserAnswers])).thenReturn(Future.successful(true))

    val application = applicationBuilder(userAnswers = None)
      .overrides(bind[EstatesConnector].toInstance(estatesConnector))
      .overrides(bind[SessionRepository].toInstance(sessionRepository))
      .build()

    val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

    val result = route(application, request).value

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual routes.CheckDetailsController.onPageLoad().url

    application.stop()
  }
}
