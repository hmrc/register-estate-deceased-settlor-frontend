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
import controllers.actions._
import javax.inject.Inject
import models.UserAnswers
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
//                                        connector: TrustConnector,
//                                        trustStoreConnector: TrustStoreConnector,
                                        val appConfig: FrontendAppConfig,
                                        sessionRepository: SessionRepository,
                                        printHelper: DeceasedSettlorPrintHelper,
                                        mapper: DeceasedSettlorMapper,
                                        nameAction: NameRequiredAction,
//                                        extractor: DeceasedSettlorExtractor,
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

//  def extractAndRender(): Action[AnyContent] = actions.authWithData.async {
//    implicit request =>
//
//      service.getSettlors(request.userAnswers.utr) flatMap {
//        case Settlors(individuals, businesses, Some(deceased)) =>
//          for {
//            hasAdditionalSettlors <- Future.successful(individuals.nonEmpty || businesses.nonEmpty)
//            extractedF <- Future.fromTry(extractor(request.userAnswers, deceased, hasAdditionalSettlors))
//            _ <- sessionRepository.set(extractedF)
//          } yield {
//            render(extractedF, deceased.name.displayName)
//          }
//        case Settlors(_, _, None) =>
//          throw new Exception("Deceased Settlor Information not found")
//
//      }
//  }

  def onPageLoad(): Action[AnyContent] = actions.authWithName.async {
    implicit request =>
        Future.successful(render(
          request.userAnswers,
          request.name))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      mapper(request.userAnswers).map {
        deceasedSettlor => ???
//          connector.amendDeceasedSettlor(request.userAnswers.utr, deceasedSettlor).flatMap(_ =>
//            service.getSettlors(request.userAnswers.utr).flatMap { settlors =>
//              (settlors.hasAdditionalSettlors, request.userAnswers.get(AdditionalSettlorsYesNoPage)) match {
//                case (false, Some(false)) =>
//                  trustStoreConnector.setTaskComplete(request.userAnswers.utr).map(_ =>
//                    Redirect(appConfig.maintainATrustOverview)
//                  )
//                case _ =>
//                  Future.successful(Redirect(controllers.routes.AddASettlorController.onPageLoad()))
//              }
//            }
//          )
      }.getOrElse(Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate)))
  }
}
