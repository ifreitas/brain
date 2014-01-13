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
import brain.db.OrientDbServer
import com.orientechnologies.orient.client.remote.OServerAdmin
import brain.config.Config
import java.io.File

object Teste2 {

    def main(args: Array[String]): Unit = {
//        implicit val db:Graph = GraphDb.get
        try{
            OrientDbServer.start
            
            implicit val db = new OrientGraph("plocal:/Users/israelfreitas/Documents/workspace/brain/databases/brain_dev")
            
            println(Knowledge.findAll.size)
            
            db.shutdown()
            
            OrientDbServer.stop
        }
        finally{
//            db.shutdown()
        }
    }

}