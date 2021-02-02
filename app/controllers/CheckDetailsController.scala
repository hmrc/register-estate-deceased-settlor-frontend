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

package controllers

import config.{ErrorHandler, FrontendAppConfig}
import connectors.{EstatesConnector, EstatesStoreConnector}
import controllers.actions._
import javax.inject.Inject
import models.{DeceasedSettlor, UserAnswers}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import utils.mappers.DeceasedSettlorMapper
import utils.print.DeceasedSettlorPrintHelper
import viewmodels.AnswerSection
import views.html.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        estatesConnector: EstatesConnector,
                                        estatesStoreConnector: EstatesStoreConnector,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: DeceasedSettlorPrintHelper,
                                        mapper: DeceasedSettlorMapper,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  private def render(userAnswers: UserAnswers,
                     name: String)(implicit request: Request[AnyContent]): Result = {
    val section: AnswerSection = printHelper(userAnswers, name)
    Ok(view(
      section,
      name

    ))
  }

  def onPageLoad(): Action[AnyContent] = actions.authWithName.async {
    implicit request =>
        Future.successful(render(
          request.userAnswers,
          request.name))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      mapper(request.userAnswers) match {
        case Some(deceasedSettlor) =>
          estatesConnector.getDeceased() flatMap {
            case Some(DeceasedSettlor(_, _, Some(previousDateOfDeath), _, _)) =>
              if(!deceasedSettlor.dateOfDeath.contains(previousDateOfDeath)) {
                logger.info(s"[submit][Session ID: ${Session.id(hc)}]" +
                  s" previous date $previousDateOfDeath, new date: ${deceasedSettlor.dateOfDeath}")
                for {
                  redirect <- sendDeceased(deceasedSettlor)
                  _ <- estatesConnector.resetTaxLiability()
                  _ <- estatesStoreConnector.resetTaxLiabilityTask()
                } yield redirect
              } else {
                sendDeceased(deceasedSettlor)
              }
            case _ =>
              sendDeceased(deceasedSettlor)
          }
        case None =>
          logger.warn(s"[submit][Session ID: ${Session.id(hc)}] Unable to generate agent details to submit.")
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

  private def sendDeceased(deceasedSettlor : DeceasedSettlor)(implicit hc: HeaderCarrier): Future[Result] = for {
    _ <- estatesConnector.setDeceased(deceasedSettlor)
    _ <- estatesStoreConnector.setTaskComplete()
  } yield {
    Redirect(appConfig.registrationProgressUrl)
  }
}
