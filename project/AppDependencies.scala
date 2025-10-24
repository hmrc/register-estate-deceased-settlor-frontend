import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.2.0"
  private val mongoVersion     = "2.9.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % mongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % "12.7.0",
    "uk.gov.hmrc"       %% "domain-play-30"                        % "11.0.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % bootstrapVersion
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % mongoVersion,
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.18.0",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
