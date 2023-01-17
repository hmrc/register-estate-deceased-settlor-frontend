/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.actions._
import forms.NationalInsuranceNumberFormProvider
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.NationalInsuranceNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NationalInsuranceNumberView

import scala.concurrent.{ExecutionContext, Future}

class NationalInsuranceNumberController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   sessionRepository: SessionRepository,
                                                   navigator: Navigator,
                                                   actions: Actions,
                                                   formProvider: NationalInsuranceNumberFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: NationalInsuranceNumberView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider.withPrefix("deceasedSettlor.nationalInsuranceNumber")

  def onPageLoad(): Action[AnyContent] = actions.authWithName {
    implicit request =>

      val preparedForm = request.userAnswers.get(NationalInsuranceNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.name))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithName.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.name))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NationalInsuranceNumberPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NationalInsuranceNumberPage, NormalMode, updatedAnswers))
      )
  }
}
