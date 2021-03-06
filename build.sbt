name := "resumable-file-upload"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val akkaV = "2.4.9"
  val akkaGroupId = "com.typesafe.akka"
  val slickV = "3.1.1"
  Seq(
    akkaGroupId                   %%   "akka-http-core"                        % akkaV,
    akkaGroupId                   %%   "akka-http-experimental"                % akkaV,
    akkaGroupId                   %%   "akka-http-spray-json-experimental"     % akkaV,
    akkaGroupId                   %%   "akka-slf4j"                            % akkaV,
    akkaGroupId                   %%   "akka-persistence"                      % akkaV,
    akkaGroupId                   %%   "akka-persistence-query-experimental"   % akkaV,
    "com.typesafe.slick"          %%   "slick"                                 % slickV,
    "com.typesafe.slick"          %%   "slick-hikaricp"                        % slickV,
    "com.github.dnvriend"         %%   "akka-persistence-jdbc"                 % "2.6.4",
    "org.postgresql"              %    "postgresql"                            % "9.4.1209",
    "org.flywaydb"                %    "flyway-core"                           % "4.0.3",
    "ch.qos.logback"              %    "logback-classic"                       % "1.1.7"
  )
}

test in assembly := {}

assemblyOutputPath in assembly := target.value / "service.jar"

lazy val styleCheck = sys.env.get("CHECK_STYLE_DURING_TEST").map(_.toLowerCase).flatMap{
  case "true" => Some(true)
  case _ => None
}.getOrElse(false)

scalastyleConfigUrl := sys.env.get("STYLE_CHECK_CONFIG_URL").map(new URL(_))

scalastyleConfig := sys.env.get("STYLE_CHECK_CONFIG").flatMap{
  case x: String if file(x).exists => Some(file(x))
  case x: String if (baseDirectory.value / x).exists => Some(baseDirectory.value / x)
  case x: String if (target.value / x).exists => Some(target.value / x)
  case _ => None
}.getOrElse(scalastyleConfig.value)

scalastyleFailOnError := styleCheck

lazy val style = Def.taskDyn(if(styleCheck) org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("") else Def.task())

(test in Test) <<= (test in Test) dependsOn style
