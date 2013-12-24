package brain.rest

import net.liftweb.http.rest.RestHelper
import net.liftweb._
import util._
import Helpers._
import common._
import json._
import Extraction._
import scala.xml.Node
import net.liftweb.common.Empty
import net.liftweb.http.S
import net.liftweb.http.NotFoundResponse
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import brain.models.Knowledge
import com.ansvia.graph.BlueprintsWrapper._

object BrainRest extends RestHelper {
    
    serve("rest"/"knowledges" prefix{
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
        
        case id:: Nil JsonGet _ => {
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
        
        // update
        case Nil JsonPut Knowledge(knowledge) -> _ => {
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
        
    })
    
}