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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import connectors.{EstatesConnector, EstatesStoreConnector}
import models.{DeceasedSettlor, Name, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.print.DeceasedSettlorPrintHelper
import views.html.CheckDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  lazy val submitRoute: String = controllers.routes.CheckDetailsController.onSubmit().url
  private val config = injector.instanceOf[FrontendAppConfig]
  private lazy val completedRoute = config.registrationProgressUrl
  private val name: Name = Name("First", Some("Middle"), "Last")

  private val goodAnswers: UserAnswers =
    emptyUserAnswers
      .set(NamePage, name).success.value
      .set(DateOfDeathPage, LocalDate.of(2011, 10, 10)).success.value
      .set(DateOfBirthYesNoPage, true).success.value
      .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
      .set(NationalInsuranceNumberYesNoPage, true).success.value
      .set(NationalInsuranceNumberPage, "AA000000A").success.value


  "Check Your Answers Controller" must {

    "return OK and the correct view for a NINO GET" in {

      val application = applicationBuilder(userAnswers = Some(goodAnswers)).build()

      val request = FakeRequest(GET, routes.CheckDetailsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]
      val printHelper = application.injector.instanceOf[DeceasedSettlorPrintHelper]
      val answerSection = printHelper(goodAnswers, name.displayName)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          answerSection,
          name.displayName
        )(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckDetailsController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }


    "redirect to the estates progress when submitted and reset tax liability when date of death changes" in {

      val mockEstatesConnector = mock[EstatesConnector]
      val mockEstatesStoreConnector = mock[EstatesStoreConnector]

      val application =
        applicationBuilder(userAnswers = Some(goodAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[EstatesStoreConnector].toInstance(mockEstatesStoreConnector))
          .build()

      val existingDeceased = DeceasedSettlor(Name("Fred", None, "Wilson"), None, Some(LocalDate.of(2010, 10, 10)), None, Some(false), None)

      when(mockEstatesConnector.getDeceased()(any(), any())).thenReturn(Future.successful(Some(existingDeceased)))
      when(mockEstatesConnector.setDeceased(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockEstatesConnector.resetTaxLiability()(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockEstatesStoreConnector.setTaskComplete()(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockEstatesStoreConnector.resetTaxLiabilityTask()(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val request = FakeRequest(POST, submitRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual completedRoute

      application.stop()
    }

    "redirect to the estates progress when submitted and date of death does not changes" in {

      val mockEstatesConnector = mock[EstatesConnector]
      val mockEstatesStoreConnector = mock[EstatesStoreConnector]

      val application =
        applicationBuilder(userAnswers = Some(goodAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[EstatesStoreConnector].toInstance(mockEstatesStoreConnector))
          .build()

      when(mockEstatesConnector.getDeceased()(any(), any())).thenReturn(Future.successful(None))
      when(mockEstatesConnector.setDeceased(any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockEstatesStoreConnector.setTaskComplete()(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val request = FakeRequest(POST, submitRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual completedRoute

      application.stop()
    }

  }
}
