package brain.snippet

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.xml.NodeSeq
import brain.db.GraphDb
import net.liftweb.http._
import net.liftweb.http.js._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.filter.OSQLPredicate
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import brain.models.Knowledge
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.Direction
import scala.collection.mutable.StringBuilder

object App {
    def onLoad(): NodeSeq = {
        <head>
    		<script>
    			var knowledgeJsonTree = {getKnowledges};
    			jQuery(document).ready(function() {{initTree(knowledgeJsonTree);}});
    		</script>
        </head>
    }

    private def getKnowledges(): String = {
		def toJsonString(sb:StringBuilder, vertex:Vertex):String={
		    sb.append(s"{id: '${vertex.getId}', name: '${vertex.getProperty("name")}', data: {topics:[${vertex.getVertices(Direction.OUT, "divisions").map(t=>s"{id:${t.getId}, name:${t.getProperty("name")}}").mkString(",")}]}, children: [")
			sb.append(vertex.getVertices(Direction.OUT, "include").map(v=>toJsonString(new StringBuilder, v)).mkString(","))
			sb.append("]}")
			sb.toString
		}
    		
        implicit val db = GraphDb.get
        val conf = db.getVerticesOfClass("Conf").head
        val rootId:String = conf.getProperty[String]("rootId")
        val depth:Integer = conf.getProperty[Int]("depth")
        try{
            val sqlString = raw"select from (traverse out() from  $rootId while $depth <= "+ conf.getProperty("defaultDepthTraverse") +")";
            val vertices:java.lang.Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute();
            toJsonString(new StringBuilder, vertices.head)
        }
        finally{
            db.shutdown()
        }
        
    }

}