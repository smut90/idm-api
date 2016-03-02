name := "web"

version := "1.0"

credentials += Credentials("Repository Archiva Managed snapshot Repository", "repo.leapset.com", "devrobot", "123456a")

resolvers += "Repository Archiva Managed snapshot Repository" at "http://repo.leapset.com/archiva/repository/snapshot/"

scalaVersion := "2.11.7"

PlayKeys.externalizeResources := false

libraryDependencies ++= Seq( javaJdbc ,  cache , javaWs )

sources in (Compile, doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false
