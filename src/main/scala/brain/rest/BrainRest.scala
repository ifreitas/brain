package brain.rest

import com.ansvia.graph.BlueprintsWrapper._
import brain.db.GraphDb
import brain.models.Knowledge
import brain.models.Knowledge.knowledgeSetToJValue
import brain.models.Knowledge.toJson
import net.liftweb.http.OkResponse
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JValue
import brain.models.Information

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
        	    Knowledge(db.getVertex(id)).destroy
        		db.commit
        		new OkResponse
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
        	    Information(db.getVertex(id)).destroy
        		db.commit
        		new OkResponse
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