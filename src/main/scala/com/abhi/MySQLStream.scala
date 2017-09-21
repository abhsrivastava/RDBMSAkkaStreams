package com.abhi

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.MySQLProfile.api._

object MySQLStream extends App {
   implicit val actorSystem = ActorSystem()
   implicit val actorMaterializer = ActorMaterializer()
   val enableJdbcStreaming: (java.sql.Statement) => Unit = { statement ⇒
      if (statement.isWrapperFor(classOf[com.mysql.jdbc.StatementImpl])) {
         statement.unwrap(classOf[com.mysql.jdbc.StatementImpl]).enableStreamingResults()
      }
   }
   val db = Database.forConfig("mysql")
   val query = sql"select id from foo".as[Long]
   val publisher = db.stream(query.withStatementParameters(statementInit = enableJdbcStreaming))
   val source = Source.fromPublisher[Long](publisher)
   val sink = Sink.foreach[Long]{x => println(x)}
   val graph = RunnableGraph.fromGraph(GraphDSL.create(sink){implicit b => s =>
      import GraphDSL.Implicits._
      source ~> s.in
      ClosedShape
   })
   val future = graph.run()
   Await.result(future, Duration.Inf)
   Await.result(actorSystem.terminate(), Duration.Inf)
   db.close()
}