name := "admin-core"

version := "1.0"

credentials += Credentials("Repository Archiva Managed snapshot Repository", "repo.leapset.com", "devrobot", "123456a")

resolvers += "Repository Archiva Managed snapshot Repository" at "http://repo.leapset.com/archiva/repository/snapshot/"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc ,
  cache ,
  javaWs,
  javaJpa,
  "com.leapset.auth" % "leapset-auth-core" % "3.0.0-SNAPSHOT",
  "com.leapset" % "admin-beans" % "3.11.0-SNAPSHOT",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.9.Final",
  "mysql" % "mysql-connector-java" % "5.1.31"
)

PlayKeys.externalizeResources := false

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

javaOptions in Test += "-Dconfig.file=conf/test.conf"


