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
import forms.DateOfBirthFormProvider
import models.{Name, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.{DateOfBirthPage, DateOfDeathPage, NamePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DateOfBirthView

import java.time.{LocalDate, ZoneOffset}

class DateOfBirthControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new DateOfBirthFormProvider()

  private def form = formProvider.withConfig("deceasedSettlor.dateOfBirth")

  private val validAnswer = LocalDate.now(ZoneOffset.UTC)
  private val invalidAnswer: LocalDate = LocalDate.parse("2020-02-03")
  private val name = Name("FirstName", None, "LastName")
  private val dateOfDeath: LocalDate = LocalDate.parse("2019-02-03")

  private lazy val dateOfBirthRoute = routes.DateOfBirthController.onPageLoad().url

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage, name).success.value
    .set(DateOfDeathPage, dateOfDeath).success.value


  private lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, dateOfBirthRoute)

  private lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, dateOfBirthRoute)
      .withFormUrlEncodedBody(
        "value.day" -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year" -> validAnswer.getYear.toString
      )

  "DateOfBirth Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[DateOfBirthView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(DateOfBirthPage, validAnswer).success.value
        .set(NamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[DateOfBirthView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val onwardRoute = Call("GET", "/foo")

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, dateOfBirthRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[DateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name.displayName)(getRequest, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when submitted date is after date of death" in {

      val form = formProvider.withConfig("deceasedSettlor.dateOfBirth", (dateOfDeath, "afterDateOfDeath"))

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, dateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day" -> invalidAnswer.getDayOfMonth.toString,
            "value.month" -> invalidAnswer.getMonthValue.toString,
            "value.year" -> invalidAnswer.getYear.toString
          )

      val boundForm = form.bind(Map(
        "value.day" -> invalidAnswer.getDayOfMonth.toString,
        "value.month" -> invalidAnswer.getMonthValue.toString,
        "value.year" -> invalidAnswer.getYear.toString
      ))

      val view = application.injector.instanceOf[DateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, name.displayName)(getRequest, messages).toString

      application.stop()
    }


    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
