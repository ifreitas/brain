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
import brain.config.Config

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
    
    private def nameFmt = Topic.this.name.replaceAll(" ", "_")
    private def idFmt   = Topic.this.id.get.replace(":", "_").replace("#", "")
    
    private def getFileName:String = s"$nameFmt$idFmt.aiml"
    private def getCompleteName:String = Config.getKnowledgeBasePath+"/"+this.getFileName
    
    def toAiml(implicit db:Graph):Aiml = Aiml(getCompleteName, aimltoxml.aiml.Topic("*", getTeachings.flatMap(_.toAiml)))
    
    def canEqual(other: Any) = other.isInstanceOf[brain.models.Topic]
  
    override def equals(other: Any) = {
    	other match {
    		case that: brain.models.Topic => Topic.super.equals(that) && that.canEqual(Topic.this) && id == that.id && name == that.name
    		case _ => false
    	}
    }
  
    override def hashCode() = {
    	val prime = 41
    	prime * (prime * Topic.super.hashCode() + id.hashCode) + name.hashCode
    }
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
        println("Regerando base de conhecimento em: "+Config.getKnowledgeBasePath)
        if(!Config.getKnowledgeBaseDir.exists()){
            Config.getKnowledgeBaseDir.mkdirs()
        }
        Config.getKnowledgeBaseDir.listFiles().foreach(_.delete)
        findAll.filter(!_.getTeachings.isEmpty).foreach{_.toAiml.toXmlFile}
    }
}
