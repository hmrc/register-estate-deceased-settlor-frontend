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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {

  final val ENGLISH = "en"
  final val WELSH = "cy"
  final val UK_COUNTRY_CODE = "GB"

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")

  lazy val countdownLength: Int = configuration.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = configuration.get[Int]("timeout.length")

  lazy val estatesUrl: String = configuration.get[Service]("microservice.services.estates").baseUrl
  lazy val estatesStoreUrl: String = configuration.get[Service]("microservice.services.estates-store").baseUrl
  lazy val registrationProgressUrl: String = configuration.get[String]("urls.registrationProgress")

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = configuration.get[String]("location.canonical.list.allCY")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  val cacheTtlSeconds: Long = configuration.get[Long]("mongodb.timeToLiveInSeconds")
  val dropIndexes: Boolean = configuration.getOptional[Boolean]("microservice.services.features.mongo.dropIndexes").getOrElse(false)


}
