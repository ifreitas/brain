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
package brain.models

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph

import aimltoxml.aiml.Aiml

case class Knowledge(val name: String, val topics: Set[Topic], val children: Set[Knowledge]) {
	require(name != null && !name.isEmpty(), "Name is required.")
	
	def toAiml:Aiml = new KnowledgeToAimlAdapter(this).toAiml
	
	def canEqual(other: Any) = {
		other.isInstanceOf[brain.models.Knowledge]
	}
	
	override def equals(other: Any) = {
		other match {
		case that: brain.models.Knowledge => that.canEqual(Knowledge.this) && name == that.name && topics == that.topics
		case _                            => false
		}
	}
	
	override def hashCode() = {
		val prime = 41
				prime * (prime + name.hashCode) + topics.hashCode
	}
	
	def toJsonString:String = s"{id: 1, name: $name, data:{topics: [${topics.map(_.name).mkString(",")}]}, children:[${children.map(c =>c.toJsonString).mkString(",")}]}"
}

class K (name:String, topics:Set[Topic], children:Set[K])

object Knowledge{
    def getSubTree(rootId:String, depth:Int)(implicit db:OrientGraph):Option[Knowledge] = {
        require(rootId != null && !rootId.isEmpty(), "")
        
        val conf = db.getVerticesOfClass("Conf").head
        
        val sqlString = raw"select from (traverse out() from  $rootId while $depth <= "+ conf.getProperty("defaultDepthTraverse") +")"
        val vertices:Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute()
        vertices.head
        Some(new Knowledge(null, null, null))
    }

    def save(name:String, parentId:String)(implicit db:OrientGraph):Vertex={
    	val parent = db.getVertex(parentId)
    	val newVertex = db.addVertex("class:Knowledge", "name", name)
    	db.addEdge("class:Include", parent, newVertex, "Include")
    	newVertex
    }
    
    def main(args: Array[String]) {
        
        println(new Knowledge("Teste", Set.empty[Topic], Set(new Knowledge("K", Set(new Topic("T", Set.empty[Teaching])), Set.empty[Knowledge]))).toJsonString)
        
//        //implicit val db = new OrientGraph(OGraphDatabasePool.global().acquire("remote:/brain_dev", "root", "826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95"))
//        new OrientGraph(OGraphDatabasePool.global().acquire("remote:/brain_dev", "root", "826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95"))
//        implicit val db = new OrientGraph(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
//        try {
//        	var newVertex = db.addVertex("class:Knowledge", "name", "KD")
//        	println(newVertex.getId())
//        	newVertex = db.addVertex("class:Knowledge", "name", "KE")
//        	println(newVertex.getId())
//        	newVertex = db.addVertex("class:Knowledge", "name", "KF")
//        	println(newVertex.getId())
//        	newVertex = db.addVertex("class:Knowledge", "name", "KG")
//        	println(newVertex.getId())
//        	newVertex = db.addVertex("class:Knowledge", "name", "KH")
//        	println(newVertex.getId())
//        	newVertex = db.addVertex("class:Knowledge", "name", "KI")
//        	println(newVertex.getId())
//        	
//        	val root = db.getVertex("#13:0")
//        	db.addEdge("class:Include", root, newVertex, "Include")
//        }
//        finally {
//        	db.shutdown()
//        }
    }
}

class KnowledgeToAimlAdapter(knowledge: Knowledge) {
    def toAiml = Aiml(knowledge.name, knowledge.topics.map(_.toAiml))
}
