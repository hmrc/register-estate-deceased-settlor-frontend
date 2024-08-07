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

package controllers.actions

import controllers.routes
import javax.inject.Inject
import models.requests.{DataRequest, NameRequest}
import pages.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class NameRequiredAction @Inject()(val executionContext: ExecutionContext, val messagesApi: MessagesApi)
  extends ActionRefiner[DataRequest, NameRequest] with I18nSupport {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, NameRequest[A]]] = {
    Future.successful(request.userAnswers.get(NamePage) match {
      case Some(name) => Right(NameRequest[A](request, name.displayName))
      case _ => Left(Redirect(routes.NameController.onPageLoad()))
    })
  }

}
