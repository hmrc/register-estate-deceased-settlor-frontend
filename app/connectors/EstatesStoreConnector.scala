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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class EstatesStoreConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private def registerTasksUrl() = s"${config.estatesStoreUrl}/estates-store/register/tasks/deceased"

  def setTaskComplete()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.POSTEmpty[HttpResponse](registerTasksUrl())
  }

  private def resetTaxLiabilityUrl() = s"${config.estatesStoreUrl}/estates-store/register/tasks/tax-liability/reset"

  def resetTaxLiabilityTask()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.POSTEmpty[HttpResponse](resetTaxLiabilityUrl())
  }
}
