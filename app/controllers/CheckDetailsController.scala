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

import config.{ErrorHandler, FrontendAppConfig}
import connectors.{EstatesConnector, EstatesStoreConnector}
import controllers.actions._
import javax.inject.Inject
import models.UserAnswers
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
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
                                        sessionRepository: SessionRepository,
                                        printHelper: DeceasedSettlorPrintHelper,
                                        mapper: DeceasedSettlorMapper,
                                        nameAction: NameRequiredAction,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

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
          for {
            _ <- estatesConnector.setDeceased(deceasedSettlor)
            _ <- estatesStoreConnector.setTaskComplete()
          } yield {
            Redirect(appConfig.registrationProgressUrl)
          }
        case None =>
          Logger.warn("[CheckDetailsController][submit] Unable to generate agent details to submit.")
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }
}
