name := """idm_api"""

version := "1.0-SNAPSHOT"

credentials += Credentials("Repository Archiva Managed snapshot Repository", "repo.leapset.com", "devrobot", "123456a")

resolvers += "Repository Archiva Managed snapshot Repository" at "http://repo.leapset.com/archiva/repository/snapshot/"

//lazy val root = (project in file(".")).enablePlugins(PlayJava)
scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"
libraryDependencies += "junit" % "junit" % "4.11"

PlayKeys.externalizeResources := false

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .dependsOn(admin_core, api)
  .aggregate(admin_core, api)
  .settings(javaOptions in Test += "-Dconfig.file=./test/conf/testApplication.conf")

lazy val admin_core = (project in file("modules/admin-core"))
  .enablePlugins(PlayJava)
  .settings(javaOptions in Test += "-Dconfig.file=../../test/conf/testApplication.conf")

lazy val api = (project in file("modules/web"))
  .enablePlugins(PlayJava)
  .dependsOn(admin_core % "test->test;compile->compile")
  .settings(javaOptions in Test += "-Dconfig.file=../../test/conf/testApplication.conf")
