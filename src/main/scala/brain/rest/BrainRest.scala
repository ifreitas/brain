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
package brain.rest

import com.ansvia.graph.BlueprintsWrapper._
import brain.db.GraphDb
import brain.models.Knowledge
import brain.models.Knowledge.knowledgeSetToJValue
import brain.models.Knowledge.toJson
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JValue
import brain.models.Information
import brain.models.Teaching
import net.liftweb.json.JField
import net.liftweb.http.JsonResponse
import net.liftweb.json.JField
import net.liftweb.json.JString
import net.liftweb.json.JObject

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
        	    case t: Throwable => t.printStackTrace; throw t
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
        	    case t: Throwable => t.printStackTrace; throw t
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
        	    case t: Throwable => t.printStackTrace; throw t
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
        	    case t: Throwable => t.printStackTrace; throw t
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
        		case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        /*
         * INFORMATIONS
         */
        case knowledgeId :: "informations" :: Nil JsonGet _ => {
            implicit val db = GraphDb.get
            try{
                Information.findByKnowledge(Knowledge.findById(knowledgeId)) : JValue
            }
            catch{
        	    case t: Throwable => t.printStackTrace; throw t
        	}
            finally{
                db.shutdown()
            }
        }
        
              // update
        case knowledgeId :: "informations" :: id :: Nil JsonPut Information(information) -> _ => {
        	implicit val db = GraphDb.get
        	try{
        	    //TODO: retornar 404 caso este knowlege nao tenha esta information
        		val vertex = db.getVertex(id)
        		vertex.setProperty("name", information.name)
        		db.commit
        		information : JValue
        	}
        	catch{
        	    case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }

        // create
        case knowledgeId :: "informations" :: Nil JsonPost Information(information) -> _ => { 
        	implicit val db = GraphDb.get
			try{
			    //TODO: retornar 404 caso este knowlege nao tenha esta information
			    val vertex = information.save
        		db.commit
        		information.id = Some(vertex.getId().toString())
        		information : JValue
			}
        	catch{
        	    case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        //requires the id url param
        case knowledgeId :: "informations" :: id :: Nil JsonDelete _ => {
        	implicit val db = GraphDb.get
        	try{
        	    // TODO: retornar 404 caso este knowlege nao tenha esta information
        	    val information = Information(db.getVertex(id))
        	    information.destroy
        		db.commit
        		JsonResponse(information)
        	}
        	catch{
        		case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        /*
         * TEACHINGS
         */
        case knowledgeId :: "informations" :: informationId :: "teachings" :: Nil JsonGet _ => {
        	implicit val db = GraphDb.get
			try{
				Teaching.findByInformation(Information.findById(informationId)) : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        // update
        case knowledgeId :: "informations" :: informationId :: "teachings" :: id :: Nil JsonPut Teaching(teaching) -> _ => {
        	implicit val db = GraphDb.get
			try{
				//TODO: retornar 404 caso este knowlege nao tenha esta information
				val vertex = db.getVertex(id)
				vertex.setProperty("whenTheUserSays", teaching.whenTheUserSays)
				vertex.setProperty("respondingTo", teaching.respondingTo)
				vertex.setProperty("memorize", teaching.memorize)
				vertex.setProperty("say", teaching.say)
				db.commit
				teaching : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        // create
        case knowledgeId :: "informations" :: informationId :: "teachings" :: Nil JsonPost Teaching(teaching) -> _ => { 
        	implicit val db = GraphDb.get
			try{
				//TODO: retornar 404 caso este knowlege nao tenha esta information
				val vertex = teaching.save
				db.commit
				teaching.id = Some(vertex.getId().toString())
				teaching : JValue
			}
        	catch{
        		case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
        
        //requires the id url param
        case knowledgeId :: "informations" :: informationId :: "teachings" :: id :: Nil JsonDelete _ => {
        	implicit val db = GraphDb.get
			try{
				// TODO: retornar 404 caso este knowlege nao tenha esta information
				val teaching = Teaching(db.getVertex(id))
				teaching.destroy
				db.commit
				JsonResponse(teaching)
			}
        	catch{
        	case t: Throwable => t.printStackTrace; throw t
        	}
        	finally{
        		db.shutdown()
        	}
        }
    })
    
}