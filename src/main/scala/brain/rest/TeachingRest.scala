package brain.rest

import net.liftweb.http.rest.RestHelper
import net.liftweb._
import util._
import Helpers._
import common._
import json._
import Extraction._
import scala.xml.Node
import brain.models.Teaching 
import net.liftweb.common.Empty
import net.liftweb.http.S
import net.liftweb.http.NotFoundResponse
import brain.db.GraphDb
import com.ansvia.graph.BlueprintsWrapper._
import net.liftweb.http.LiftResponse
import com.tinkerpop.blueprints.Vertex


object TeachingRest extends RestHelper{
    
    implicit private def teachingToJValue(teaching:Teaching):JValue=decompose(teaching)
    implicit private def teachingSetToJValue(teachings:Set[Teaching]):JValue=decompose(teachings)
    
    serve("rest"/"informations" prefix {

        // /rest.informations/1/teachings.json
        case informationId :: "teachings" :: Nil JsonGet _ => {
            implicit val db = GraphDb.get
            try {
                JArray(Teaching.findByInformationId(informationId).toList.map(t=>t:JValue))
            }
            finally {
                db.shutdown()
            }
        }
        
        // /rest.informations/1/teachings/1.json
        case informationId :: "teachings" :: teachingId :: Nil JsonGet _ => {
            var v:Vertex =  null
            implicit val db = GraphDb.get
            try { v = Teaching(null,"what","hei","on=true",":*").save()  }
            finally { db.shutdown() }
            Teaching(v.getId().toString(), "what","hei","on=true",":*"):JValue 
        }
    })

}