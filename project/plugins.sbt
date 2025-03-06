resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

addSbtPlugin("uk.gov.hmrc"         % "sbt-auto-build"        % "3.24.0")
addSbtPlugin("uk.gov.hmrc"         % "sbt-distributables"    % "2.6.0")
addSbtPlugin("org.playframework"   % "sbt-plugin"            % "3.0.6")
addSbtPlugin("org.scalastyle"      % "scalastyle-sbt-plugin" % "1.0.0" exclude ("org.scala-lang.modules", "scala-xml_2.12"))
addSbtPlugin("org.scoverage"       % "sbt-scoverage"         % "2.0.12")
addSbtPlugin("io.github.irundaia"  % "sbt-sassify"           % "1.5.2")
addSbtPlugin("com.github.sbt" % "sbt-concat"            % "1.0.0")
addSbtPlugin("com.github.sbt"    % "sbt-uglify"            % "3.0.0")
addSbtPlugin("com.github.sbt"    % "sbt-digest"            % "2.0.0")
addSbtPlugin("com.timushev.sbt"    % "sbt-updates"           % "0.6.4")
