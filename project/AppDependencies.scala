import sbt.*

object AppDependencies {

  val bootstrapVersion = "7.22.0"
  val mongoVersion = "1.3.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"             % mongoVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"             % "7.23.0-play-28",
    "uk.gov.hmrc"             %% "domain"                         % "8.3.0-play-28",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"     % bootstrapVersion
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.17.0",
    "org.scalatest"               %% "scalatest"                % "3.2.17",
    "org.jsoup"                   %  "jsoup"                    % "1.16.1",
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.27",
    "org.wiremock"                %  "wiremock-standalone"      % "3.2.0",
    "io.github.wolfendale"        %% "scalacheck-gen-regexp"    % "1.1.0",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
