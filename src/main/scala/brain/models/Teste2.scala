package brain.models

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.ansvia.graph.BlueprintsWrapper._
import brain.db.GraphDb
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import scala.collection.JavaConversions.iterableAsScalaIterable
import com.tinkerpop.blueprints.Vertex

object Teste2 {

    def main(args: Array[String]): Unit = {
        implicit val db:Graph = new OrientGraph(OGraphDatabasePool.global().acquire("remote:/brain_dev", "root", "826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95"))
        try{
//        	val ts = db.query.has("id", "#9:18").vertices().toList
//        	println(ts.size)
            
//            val k = Knowledge(db.getVertex("9:0"))
//            k.getVertex.pipe.out("include").iterator.toSet[Vertex].foreach(v => println(Knowledge(v))) //toList().foreach(v => println(Knowledge(v)))
            
            //println(Information(db.getVertex("9:5")).getVertex.pipe.out("include").toList.map(v=>Teaching(v)).size)
            println(Information(db.getVertex("9:5")).getVertex.pipe.out("include").iterator.next())
            
        }
        finally{
            db.shutdown()
        }
    }

}