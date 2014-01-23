/**
 * Copyright 2013 Israel Freitas (israel.araujo.freitas@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import brain.models.Configuration

object App {
    def onLoad(): NodeSeq = {
        
        var knowledges = ""
        try{
            <head>
    		<script>
    			var knowledgeJsonTree = {getKnowledges};
    			jQuery(document).ready(function() {{initTree(knowledgeJsonTree);}});
    		</script>
        </head>
        }
        catch{
            case t : Throwable => {
                t.printStackTrace()
                <head>
                	<script>
    					Log.error("Unable to load the Knowledge Base Tree. Sorry.")
                	</script>
                </head>
            }
        }
    }

    private def getKnowledges(): String = {
		def toJsonString(sb:StringBuilder, root:Vertex):String={
		    
		    sb.append("[")
			toJsonStringRecursive(sb, root)
		    sb.append("]")
		    
		    def toJsonStringRecursive(sb:StringBuilder, vertex:Vertex):Unit={
	    		sb.append(s"{id: '${vertex.getId.toString().replace("#", "")}', name: '${vertex.getProperty("name")}', data: {topics:[${vertex.getVertices(Direction.OUT, "divisions").map(t=>s"{id:${t.getId}, name:${t.getProperty("name")}}").mkString(",")}]}, adjacencies: [")
	    		sb.append(vertex.getVertices(Direction.OUT, "include").map(v=>"'"+v.getId().toString().replace("#", "")+"'").mkString(","))
	    		sb.append("]},")
	    		vertex.getVertices(Direction.OUT, "include").foreach(v=>toJsonStringRecursive(sb, v))
		    }
		    
			sb.toString
		}
    		
        implicit val db = GraphDb.get
        
        val conf = db.getVertices("defaultDepthTraverse", 3).head //db.getVerticesOfClass("Conf").head
        val rootId:String = conf.getProperty[String]("rootId")
        val depth:Integer = conf.getProperty[Int]("depth")
        try{
            //val sqlString = raw"select from (traverse out() from  $rootId while $depth <= "+ conf.getProperty("defaultDepthTraverse") +")";
        	val sqlString = raw"select from (traverse out() from  $rootId)";
            val vertices:java.lang.Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute();
            toJsonString(new StringBuilder, vertices.head)
        }
        finally{
            db.shutdown()
        }
        
    }

}