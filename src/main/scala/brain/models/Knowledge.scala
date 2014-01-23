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

case class Knowledge(val name: String) extends DbObject with Equals {
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
	    getTopics foreach (_ destroy)
        getChildren foreach (_ destroy)
	    this.reload // Previne "OConcurrentModificationException: Cannot DELETE the record #9:18 because the version is not the latest. Probably you are deleting an old record or it has been modified by another user (db=v23 your=v21)"
        db removeVertex getVertex
    }
	
	def getTopics()(implicit db:Graph):Set[Topic] = Topic.findByKnowledge(this)
	def getChildren(implicit db:Graph):Set[Knowledge] = Knowledge.getChildren(this)
	
	def canEqual(other: Any) = other.isInstanceOf[brain.models.Knowledge]
  
	override def equals(other: Any) = {
		other match {
			case that: brain.models.Knowledge => Knowledge.super.equals(that) && that.canEqual(Knowledge.this) && id == that.id && name == that.name
			case _ => false
		}
	}
  
	override def hashCode() = {
		val prime = 41
		prime * (prime * Knowledge.super.hashCode() + id.hashCode) + name.hashCode
	}
}

object Knowledge extends PersistentName {
    private implicit val formats = net.liftweb.json.DefaultFormats

    //implicit def toJson(knowledge: Knowledge): JValue = JObject(
	//	JField("id", JString(knowledge.id.get.replace("#", ""))) 	 ::
    //  JField("name", JString(knowledge.name))		 ::
    //  JField("data", JObject(List.empty[JField]))  :: 
    //	JField("adjacencies", JArray(List.empty[JValue])):: 
    //  Nil
    //)
    implicit def toJson(knowledge: Knowledge): JValue = {
        import net.liftweb.json.JsonDSL._ 
        import net.liftweb.json.JsonAST._
        
        ("id" -> knowledge.id.get.replace("#", "")) ~
        ("name" -> knowledge.name) ~
        ("data" -> JObject(List.empty[JField])) ~
        ("adjacencies" -> JArray(List.empty[JValue]))
        
    }
    
    implicit def knowledgeSetToJValue(knowledges: Set[Knowledge]): JValue = JArray(knowledges.map(toJson).toList)
    
    def findAll()(implicit db:Graph):Set[Knowledge] = query().vertices().map(Knowledge(_)).toSet
    def findAll2()(implicit db:Graph):List[Knowledge] = query().vertices().map(Knowledge(_)).toList
    
    def findById(id:String)(implicit db:Graph):Knowledge = Knowledge(db.getVertex(id))
    
    def root()(implicit db:Graph):Knowledge=Knowledge(Knowledge.query.vertices.head)
    
    def getChildren(knowledge:Knowledge)(implicit db:Graph):Set[Knowledge] = {
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
