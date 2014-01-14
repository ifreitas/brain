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
import com.ansvia.graph.BlueprintsWrapper._
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import aimltoxml.aiml.Aiml
import com.tinkerpop.blueprints.Graph
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import net.liftweb.json.Extraction
import scala.xml.Node
import net.liftweb.json._
import com.tinkerpop.blueprints.TransactionalGraph
import com.ansvia.graph.annotation.Persistent
import net.liftweb.http.Req
import brain.db.GraphDb

case class Knowledge(val name: String) extends DbObject {
	require(!name.isEmpty(), "Name is required.")
	
	var id:Option[String] = None
	var parentId:Option[String] = None
	    
	override def toString():String  = s"Knowledge: $name ($id)"
	
	def save()(implicit db:TransactionalGraph) = transact{
        val that = super.save()
        parentId map { pId => db.getVertex(pId) --> "include" --> that }
        that
    }
	
	def isRoot(implicit db:TransactionalGraph) = Knowledge.root.id == this.id
	
	def destroy()(implicit db:TransactionalGraph):Unit = transact{
	    if(this.isRoot) throw new IllegalArgumentException("Unable to delete the root knowledge.")
	    getInformations foreach (_ destroy)
        getNestedKnowledges foreach (_ destroy)
        db removeVertex getVertex
    }
	
	def getInformations()(implicit db:Graph):Set[Information] = Information.findByKnowledge(this)
	def getNestedKnowledges(implicit db:Graph):Set[Knowledge] = Knowledge.getNestedKnowledges(this)
}

object Knowledge extends PersistentName {
    private implicit val formats = net.liftweb.json.DefaultFormats

    implicit def toJson(knowledge: Knowledge): JValue = JObject(
		JField("id", JString(knowledge.id.get.replace("#", ""))) 	 ::
        JField("name", JString(knowledge.name))		 ::
        JField("data", JObject(List.empty[JField]))  :: 
    	JField("adjacencies", JArray(List.empty[JValue])):: 
        Nil
    )
    
    implicit def knowledgeSetToJValue(knowledges: Set[Knowledge]): JValue = JArray(knowledges.map(toJson).toList)
    
    def findAll()(implicit db:Graph):Set[Knowledge] = query().vertices().toSet[Vertex].map(v=>Knowledge(v))
    
    def findById(id:String)(implicit db:Graph):Knowledge = Knowledge(db.getVertex(id))
    
    def root()(implicit db:Graph):Knowledge=Knowledge(Knowledge.query.vertices.head)
    
    def getNestedKnowledges(knowledge:Knowledge)(implicit db:Graph):Set[Knowledge] = {
        knowledge.getVertex.pipe.out("include").iterator.toSet[Vertex].map(v=>Knowledge(v))
    }
    
    def apply(in: JValue):Box[Knowledge] = Helpers.tryo{
        try {
        	val knowledge:Knowledge = new Knowledge((in \ "name").values.toString)
	        (in \ "id") match {
        	    case id: JString => knowledge.id = Some(id.values)
        	    case _ => knowledge.id = None
        	}
	        (in \ "parentId") match {
        	    case parentId: JString => knowledge.parentId = Some(parentId.values)
        	    case _ => knowledge.parentId = None
        	}
	        knowledge
		}
        catch{
            case t:Throwable => t.printStackTrace(); throw t
        }
    }
    def unapply(in:JValue):Option[Knowledge] = apply(in)
    
    def unapply(in:Any):Option[(Option[String], String, Option[String])] = {
        in match {
            case i : Knowledge => {
               return Some((i.id, i.name, i.parentId))
            }
            case i : String => {
            	implicit val db = GraphDb.get
				try{
	        		val k = Knowledge.findById(i)
	        		Some(k.id, k.name, k.parentId)
				}
	        	catch{
	        	    case t: Throwable => None
	        	}
	        	finally{
	        		db.shutdown()
	        	}
            }
            case _ => None
        }
    }
    
    def apply(vertex:Vertex):Knowledge = {
        val knowledge = vertex.toCC[Knowledge].get
        knowledge.id = Some(vertex.getId.toString)
        knowledge
    }
}
