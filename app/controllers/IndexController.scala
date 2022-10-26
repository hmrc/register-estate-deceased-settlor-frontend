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

package controllers

import connectors.EstatesConnector
import controllers.actions.Actions
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.extractors.DeceasedExtractor

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 actions: Actions,
                                 repository: SessionRepository,
                                 estatesConnector: EstatesConnector,
                                 deceasedExtractor: DeceasedExtractor
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = actions.authWithSession.async {
    implicit request =>

      val userAnswers: UserAnswers = UserAnswers(request.internalId)

      estatesConnector.getDeceased() flatMap {
        case Some(deceased) =>
          for {
            updatedAnswers <- Future.fromTry(deceasedExtractor(deceased, userAnswers))
            _ <- repository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.CheckDetailsController.onPageLoad())
          }

        case None =>
          repository.set(userAnswers).map { _ =>
            Redirect(controllers.routes.NameController.onPageLoad())
          }
      }
  }

  }
