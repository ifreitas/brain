package brain.models

import brain.db.GraphDb
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import scala.collection.JavaConversions.iterableAsScalaIterable

object Teste2 {

    def main(args: Array[String]): Unit = {
        implicit val db:Graph = new OrientGraph(OGraphDatabasePool.global().acquire("remote:/brain_dev", "root", "826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95"))
        try{
        	val ts = db.query.has("id", "#9:18").vertices().toList
        	println(ts.size)
        }
        finally{
            db.shutdown()
        }
    }

}