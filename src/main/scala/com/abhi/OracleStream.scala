package com.abhi

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.OracleProfile.api._

/**
  * Created by ASrivastava on 9/21/17.
  */
object OracleStream extends App {
   implicit val actorSystem = ActorSystem()
   implicit val actorMaterializer = ActorMaterializer()
   val db = Database.forConfig("oracle")
   val query = sql"select member_id from erwadm.member where PRIVATE_LABEL_ID = 3".as[Long]
   val publisher = db.stream(query)
   val source = Source.fromPublisher[Long](publisher)
   val sink = Sink.foreach[Long](x => println(x))
   val graph = RunnableGraph.fromGraph(GraphDSL.create(sink){implicit builder => s =>
      import GraphDSL.Implicits._
      source ~> s.in
      ClosedShape
   })
   val future = graph.run()
   Await.result(future, Duration.Inf)
   db.close()
}
