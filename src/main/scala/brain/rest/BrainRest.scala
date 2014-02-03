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
package brain.rest

import com.ansvia.graph.BlueprintsWrapper._
import brain.db.GraphDb
import brain.models.Knowledge
import brain.models.Topic
import brain.models.Teaching
import net.liftweb.http.JsonResponse
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import brain.config.Config

object BrainRest extends RestHelper {
    
    serve("rest"/"knowledges" prefix{
        
        /*
         * KNOWLEDGE
         */
        case Nil JsonGet _ => {
            implicit val db = GraphDb.get
            try{
                Knowledge.findAll : JValue
            }
            catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
            finally{
                db.shutdown()
            }
        }
        
        //TODO change to Post!
        case "apply" :: Nil JsonGet _ => {
        	implicit val db = GraphDb.get
			try{
				Topic.createTheKnowledgeBase
				JObject(JField("success", JString("true")) :: JField("path", JString(Config.getKnowledgeBasePath)) :: Nil)
			}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        


        case id :: Nil JsonGet _ => {
            println("ID: "+id)
        	implicit val db = GraphDb.get
			try{
        		Knowledge.findById(id) : JValue
			}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        // update
        case id :: Nil JsonPut Knowledge(knowledge) -> _ => {
        	implicit val db = GraphDb.get
        	try{
        		val vertex = db.getVertex(knowledge.id.get)
        		vertex.setProperty("name", knowledge.name)
        		db.commit
        		knowledge : JValue
        	}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }

        // create
        case Nil JsonPost Knowledge(knowledge) -> _ => { 
        	implicit val db = GraphDb.get
			try{
			    val vertex = knowledge.save
        		db.commit
        		knowledge.id = Some(vertex.getId().toString())
        		knowledge : JValue
			}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        //requires the id url param
        case id :: Nil JsonDelete _ => {
        	implicit val db = GraphDb.get
        	try{
        	    val knowledge = Knowledge(db.getVertex(id))
        	    knowledge.destroy
        		db.commit
        		JsonResponse(knowledge)
        	}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        /*
         * TOPICS
         */
        case knowledgeId :: "topics" :: Nil JsonGet _ => {
            implicit val db = GraphDb.get
            try{
                Topic.findByKnowledge(Knowledge.findById(knowledgeId)) : JValue
            }
            catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
            finally{
                db.shutdown()
            }
        }
        
              // update
        case knowledgeId :: "topics" :: id :: Nil JsonPut Topic(topic) -> _ => {
        	implicit val db = GraphDb.get
        	try{
        	    //TODO: retornar 404 caso este knowlege nao tenha esta information
        		val vertex = db.getVertex(id)
        		vertex.setProperty("name", topic.name)
        		db.commit
        		topic : JValue
        	}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }

        // create
        case knowledgeId :: "topics" :: Nil JsonPost Topic(topic) -> _ => { 
        	implicit val db = GraphDb.get
			try{
			    //TODO: retornar 404 caso este knowlege nao tenha esta information
			    val vertex = topic.save
        		db.commit
        		topic.id = Some(vertex.getId().toString())
        		topic : JValue
			}
        	catch{
        	    case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        //requires the id url param
        case knowledgeId :: "topics" :: id :: Nil JsonDelete _ => {
        	implicit val db = GraphDb.get
        	try{
        	    // TODO: retornar 404 caso este knowlege nao tenha esta information
        	    val topic = Topic(db.getVertex(id))
        	    topic.destroy
        		db.commit
        		JsonResponse(topic)
        	}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        /*
         * TEACHINGS
         */
        case knowledgeId :: "topics" :: topicId :: "teachings" :: Nil JsonGet _ => {
        	implicit val db = GraphDb.get
			try{
				Teaching.findByTopic(Topic.findById(topicId)) : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        // update
        case knowledgeId :: "topics" :: topicId :: "teachings" :: id :: Nil JsonPut Teaching(teaching) -> _ => {
        	implicit val db = GraphDb.get
			try{
				//TODO: retornar 404 caso este knowlege nao tenha esta information
			    teaching.update
				
				teaching : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        // create
        case knowledgeId :: "topics" :: topicId :: "teachings" :: Nil JsonPost Teaching(teaching) -> _ => { 
        	implicit val db = GraphDb.get
			try{
				//TODO: retornar 404 caso este knowlege nao tenha esta information
				val vertex = teaching.save
				db.commit
				teaching.id = Some(vertex.getId().toString())
				teaching : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        //requires the id url param
        case knowledgeId :: "topics" :: topicId :: "teachings" :: id :: Nil JsonDelete _ => {
        	implicit val db = GraphDb.get
			try{
				// TODO: retornar 404 caso este knowlege nao tenha esta information
				val teaching = Teaching(db.getVertex(id))
				teaching.destroy
				db.commit
				JsonResponse(teaching)
			}
        	catch{
        		case t: Throwable => t.printStackTrace; JsonResponse((("success"->false) ~ ("msg"->t.getMessage)), 200)
        	}
        	finally{
        		db.shutdown()
        	}
        }
    })
    
}