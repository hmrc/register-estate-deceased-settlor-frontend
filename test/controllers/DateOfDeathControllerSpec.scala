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
import forms.DateOfDeathFormProvider
import models.Name
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.{DateOfDeathPage, NamePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DateOfDeathView

import java.time.LocalDate

class DateOfDeathControllerSpec extends SpecBase with MockitoSugar {

  private def onwardRoute = Call("GET", "/foo")

  private val validAnswer = LocalDate.of(2018, 2, 24)
  private val name = Name("FirstName", None, "LastName")

  private val formProvider = new DateOfDeathFormProvider()

  private def form = formProvider.withConfig("deceasedSettlor.dateOfDeath")

  private lazy val dateOfDeathRoute = routes.DateOfDeathController.onPageLoad().url

  private val userAnswersWithName = emptyUserAnswers.set(NamePage, name)
    .success.value

  private def getGETRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, dateOfDeathRoute)

  private def getPOSTRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, dateOfDeathRoute)
      .withFormUrlEncodedBody(
        "value.day" -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year" -> validAnswer.getYear.toString
      )

  "DateOfDeath Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val result = route(application, getGETRequest).value

      val view = application.injector.instanceOf[DateOfDeathView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, name.displayName)(getGETRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(DateOfDeathPage, validAnswer).success.value
        .set(NamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[DateOfDeathView]

      val result = route(application, getGETRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), name.displayName)(getGETRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val result = route(application, getPOSTRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithName)).build()

      val request =
        FakeRequest(POST, dateOfDeathRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DateOfDeathView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name.displayName)(getGETRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getGETRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getPOSTRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
