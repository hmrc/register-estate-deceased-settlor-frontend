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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, urlEqualTo, _}
import models.{DeceasedSettlor, Name, NationalInsuranceNumber}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.Application
import play.api.libs.json.Json
import play.api.test.Helpers.{CONTENT_TYPE, _}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.ExecutionContext

class EstatesConnectorSpec extends SpecBase
  with ScalaFutures
  with IntegrationPatience
  with WireMockHelper {

  private val deceased = DeceasedSettlor(
    Name("First", None, "Last"),
    Some(LocalDate.of(1972, 9, 18)),
    Some(LocalDate.of(2018, 2, 23)),
    Some(NationalInsuranceNumber("AA111111B")),
    None
  )

  private val requestBody = Json.toJson(deceased)

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "estates connector" must {

    "submit deceased as JSON" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      val json = Json.obj()
      val deceased = DeceasedSettlor(
        Name("First", None, "Last"),
        Some(LocalDate.of(1972, 9, 18)),
        Some(LocalDate.of(2018, 2, 23)),
        Some(NationalInsuranceNumber("AA111111B")),
        None
      )

      val requestBody = Json.toJson(deceased)

      server.stubFor(
        post(urlEqualTo("/estates/deceased"))
          .withHeader(CONTENT_TYPE, containing("application/json"))
          .withRequestBody(equalTo(requestBody.toString))

          .willReturn(okJson(json.toString))
      )

      val futureResult = connector.setDeceased(deceased)

      whenReady(futureResult) {
        r =>
          r.status mustBe OK
      }

      application.stop()
    }

    "throw exception on internal server error" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        post(urlEqualTo("/estates/deceased"))
          .withHeader(CONTENT_TYPE, containing("application/json"))
          .withRequestBody(equalTo(requestBody.toString))

          .willReturn(serverError)
      )

      val futureResult = connector.setDeceased(deceased)

      whenReady(futureResult) {
        r =>
          r.status mustBe INTERNAL_SERVER_ERROR
      }

      application.stop()
    }

    "throw exception on bad request" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        post(urlEqualTo("/estates/deceased"))
          .withHeader(CONTENT_TYPE, containing("application/json"))
          .withRequestBody(equalTo(requestBody.toString))

          .willReturn(badRequest)
      )

      val futureResult = connector.setDeceased(deceased)

      whenReady(futureResult) {
        r =>
          r.status mustBe BAD_REQUEST
      }

      application.stop()
    }

    "read submitted deceased as JSON" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      val deceased = DeceasedSettlor(
        Name("First", None, "Last"),
        Some(LocalDate.of(1972, 9, 18)),
        Some(LocalDate.of(2018, 2, 23)),
        Some(NationalInsuranceNumber("AA111111B")),
        None
      )

      val submittedDeceased = Json.toJson(deceased)

      server.stubFor(
        get(urlEqualTo("/estates/deceased"))
          .willReturn(okJson(submittedDeceased.toString))
      )

      val futureResult = connector.getDeceased()

      whenReady(futureResult) {
        r =>
          r mustBe Some(deceased)
      }

      application.stop()
    }

    "read return None when there is no submitted deceased to get" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        get(urlEqualTo("/estates/deceased"))
          .willReturn(okJson(Json.obj().toString))
      )

      val futureResult = connector.getDeceased()

      whenReady(futureResult) {
        r =>
          r mustBe None
      }

      application.stop()
    }

    "call reset endpoint for tax liability" in {

      val application: Application = createApplication

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        post(urlEqualTo("/estates/reset-tax-liability"))
          .willReturn(ok())
      )

      val futureResult = connector.resetTaxLiability()

      whenReady(futureResult) {
        r =>
          r.status mustBe OK
      }

      application.stop()
    }
  }

  private def createApplication = {
    val application = applicationBuilder()
      .configure(
        Seq(
          "microservice.services.estates.port" -> server.port(),
          "auditing.enabled" -> false
        ): _*
      ).build()
    application
  }
}
