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
import scala.xml.Text

object Teste2 {

    def main(args: Array[String]): Unit = {
        implicit val db:Graph = new OrientGraph(OGraphDatabasePool.global().acquire("remote:/brain_dev", "root", "826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95"))
        try{
            Information.createTheKnowledgeBase
        }
        finally{
            db.shutdown()
        }
    }

}