/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Israel Freitas -- ( gmail => israel.araujo.freitas)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
            case t : Throwable => <head>
    		<script>
    			Log.error("Unable to load the Knowledge Base Tree. Sorry.")
    		</script>
        </head>
        }
    }

    private def getKnowledges(): String = {
		def toJsonString(sb:StringBuilder, vertex:Vertex):String={
		    sb.append(s"{id: '${vertex.getId}', name: '${vertex.getProperty("name")}', data: {topics:[${vertex.getVertices(Direction.OUT, "divisions").map(t=>s"{id:${t.getId}, name:${t.getProperty("name")}}").mkString(",")}]}, children: [")
			sb.append(vertex.getVertices(Direction.OUT, "include").map(v=>toJsonString(new StringBuilder, v)).mkString(","))
			sb.append("]}")
			sb.toString
		}
    		
        implicit val db = GraphDb.get
        
        val conf = db.getVertices("defaultDepthTraverse", 3).head //db.getVerticesOfClass("Conf").head
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