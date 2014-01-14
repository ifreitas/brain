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
import net.liftweb.json._
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import com.tinkerpop.blueprints.TransactionalGraph
import aimltoxml.aiml.Aiml
import aimltoxml.aiml.Category
import java.io.File

/**
 * Equivalent to an AIML file.
 */
case class Topic(val name:String) extends DbObject {
    
    var id:Option[String] = None
	var knowledgeId:Option[String] = None
    
	def save()(implicit db:TransactionalGraph) = transact{
        val that = super.save()
        db.getVertex(knowledgeId.get) --> "topics" --> that
        that
    }
    
    def destroy() (implicit db:TransactionalGraph) = transact{
	    getTeachings foreach (_ destroy)
        db removeVertex getVertex
    }
    
    def getTeachings()(implicit db:Graph):Set[Teaching] = Teaching.findByTopic(Topic.this)
    
    override def toString():String  = s"Topic: $name ($id)"
    
    private def getCompleteName:String = "knowledge_base/"+Topic.this.name.replaceAll(" ", "_")+Topic.this.id.get.replace(":", "_").replace("#", "")+".aiml"
    
    def toAiml(implicit db:Graph):Aiml = Aiml(getCompleteName, aimltoxml.aiml.Topic("*", getTeachings.flatMap(_.toAiml)))
}

object Topic extends PersistentName {
    private implicit val formats = net.liftweb.json.DefaultFormats

    implicit def toJson(topic: Topic): JValue = JObject(
		JField("id", JString(topic.id.get.replace("#", ""))) 	 ::
        JField("name", JString(topic.name))		 ::
        JField("knowledgeId", JString(topic.knowledgeId.get.replace("#", "")))  :: 
        Nil
    )
    
    implicit def topicSetToJValue(topics: Set[Topic]): JValue = JArray(topics.map(toJson).toList)
    
    def findAll()(implicit db:Graph):Set[Topic] = query().vertices().toSet[Vertex].map(v=>Topic(v))
    
    def findById(id:String)(implicit db:Graph):Topic = Topic(db.getVertex(id))
    
    def findByKnowledge(knowledge:Knowledge)(implicit db:Graph):Set[Topic] = knowledge.getVertex.pipe.out("topics").iterator.toSet[Vertex].map(v=>Topic(v))
    
    def apply(in: JValue):Box[Topic] = Helpers.tryo{
        try {
            val topic:Topic = new Topic((in \ "name").values.toString)
	        (in \ "id") match {
                case id: JString => topic.id = Some(id.values)
                case _ => topic.id = None
            }
	        (in \ "knowledgeId") match {
                case knowledgeId: JString => topic.knowledgeId = Some(knowledgeId.values)
                case _ => topic.knowledgeId = None
            }
	        topic
		}
        catch{
            case t:Throwable => t.printStackTrace(); throw t
        }
    }
    def unapply(in:JValue):Option[Topic] = apply(in)
    
    def unapply(in:Any):Option[(Option[String], String, Option[String])] = {
        in match {
            case i : Topic => {
               return Some((i.id, i.name, i.knowledgeId))
            }
            case i : String => {
            	implicit val db = GraphDb.get
				try{
	        	    val topic = Topic.findById(i)
	        	    Some(topic.id, topic.name, topic.knowledgeId)
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
    
    def apply(vertex:Vertex)(implicit db:Graph):Topic = {
        val topic = vertex.toCC[Topic].get
        topic.id = Some(vertex.getId.toString)
        topic.knowledgeId = Some(vertex.pipe.in("topics").iterator.next().getId().toString())
        topic
    }
    
    def createTheKnowledgeBase()(implicit db:Graph):Unit = {
        val knowledgeBase = new File("knowledge_base")
        println("Regerando base de conhecimento em: "+knowledgeBase.getAbsolutePath())
        knowledgeBase.listFiles().foreach(_.delete)
        findAll.filter(!_.getTeachings.isEmpty).foreach{_.toAiml.toXmlFile}
    }
}
