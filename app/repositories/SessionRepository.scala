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

package repositories


import models.UserAnswers
import play.api.libs.json._
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.JSONCollection
import java.time.LocalDateTime
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject()(
                                          override val mongo: ReactiveMongoApi,
                                          config: Configuration
                                        )(override implicit val ec: ExecutionContext) extends SessionRepository
  with IndexManager {

  implicit final val jsObjectWrites: OWrites[JsObject] = OWrites[JsObject](identity)

  override val collectionName: String = "user-answers"

  override val dropIndexes: Boolean =
    config.get[Boolean]("microservice.services.features.mongo.dropIndexes")

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] =
    for {
      _ <- ensureIndexes
      res <- mongo.database.map(_.collection[JSONCollection](collectionName))
    } yield res

  private val lastUpdatedIndex = MongoIndex(
    key = Seq("updatedAt" -> IndexType.Ascending),
    name    = "user-answers-last-updated-index",
    expireAfterSeconds = Some(cacheTtl)
  )

  private val internalAuthIdIndex = MongoIndex(
    key = Seq("_id" -> IndexType.Ascending),
    name = "internal-auth-id-index"
  )

  private lazy val ensureIndexes = {
    logger.info("Ensuring collection indexes")
    for {
      collection              <- mongo.database.map(_.collection[JSONCollection](collectionName))
      createdLastUpdatedIndex <- collection.indexesManager.ensure(lastUpdatedIndex)
      createdIdIndex          <- collection.indexesManager.ensure(internalAuthIdIndex)
    } yield createdLastUpdatedIndex && createdIdIndex
  }


  override def get(id: String): Future[Option[UserAnswers]] =
    collection.flatMap(_.find(Json.obj("_id" -> id), None).one[UserAnswers])

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.internalAuthId
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    collection.flatMap {
      _.update(ordered = false)
        .one(selector, modifier, upsert = true).map {
          lastError =>
            lastError.ok
      }
    }
  }
}

trait SessionRepository {

  def get(id: String): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
