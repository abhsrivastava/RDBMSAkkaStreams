name := "RDBMSAkkaStreams"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
   "com.typesafe.slick" %% "slick" % "3.2.1",
   "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
   "oracle" % "ojdbc7_2.11" % "7.0.0",
   "com.typesafe.akka" %% "akka-stream" % "2.5.4",
   "mysql" % "mysql-connector-java" % "5.1.44",
   "ch.qos.logback" % "logback-classic" % "1.2.3"
)
        