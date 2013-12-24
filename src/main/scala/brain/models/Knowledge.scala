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
import net.liftweb.json.JValue
import net.liftweb.util.Helpers
import net.liftweb.json.Extraction
import scala.xml.Node
import net.liftweb.json.Xml
import net.liftweb.json.JObject
import net.liftweb.json.JObject
import net.liftweb.json.JObject
import com.tinkerpop.blueprints.TransactionalGraph
import com.ansvia.graph.annotation.Persistent
import net.liftweb.json.JField
import net.liftweb.json.JString
import net.liftweb.json.JObject
import net.liftweb.json.JField
import net.liftweb.json.JArray
import net.liftweb.json.JValue
import net.liftweb.json.JObject
import net.liftweb.json.JField

case class Knowledge(val name: String) extends DbObject {
	require(!name.isEmpty(), "Name is required.")
	
	var id:Option[String] = None
	var parentId:Option[String] = None
	    
	override def toString():String  = "knowledge: "+name
	
	def save()(implicit db:TransactionalGraph)={
	    transact{
	        val that = super.save()
	        parentId map { pId => db.getVertex(pId) --> "include" --> that }
	        that
	    }
	}
}

object Knowledge extends PersistentName {
    private implicit val formats = net.liftweb.json.DefaultFormats

    implicit def toJson(knowledge: Knowledge): JValue = JObject(
		JField("id", JString(knowledge.id.get)) 	 ::
        JField("name", JString(knowledge.name))		 ::
        JField("data", JObject(List.empty[JField]))  :: 
    	JField("chidren", JArray(List.empty[JValue])):: 
        Nil
    )
    
    implicit def knowledgeSetToJValue(knowledges: Set[Knowledge]): JValue = JArray(knowledges.map(toJson).toList)
    
    def findAll()(implicit db:Graph):Set[Knowledge] = query().vertices().toSet[Vertex].map(v=>Knowledge(v))
    
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
    def apply(vertex:Vertex):Knowledge = {
        val knowledge = new Knowledge(vertex.getProperty("name"))
        knowledge.id = Some(vertex.getId.toString)
        println("(vertex)"+knowledge)
        knowledge
    }
}


class KnowledgeToAimlAdapter(knowledge: Knowledge) {
//    def toAiml = Aiml(knowledge.name, knowledge.topics.map(_.toAiml))
}
