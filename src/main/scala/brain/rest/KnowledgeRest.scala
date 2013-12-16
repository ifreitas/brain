package brain.rest

import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JValue
import net.liftweb.json.Extraction._


//case class Information(id:String, name:String, knowledgeId:String)
////case class Knowledge(id:String, parentId:String, name:String, informations:Set[Information])
//
//object KnowledgeRest extends RestHelper{
//    
//    implicit private def knowledgeToJValue(knowledge:Knowledge):JValue=decompose(knowledge)
//    
//    serve("rest"/"knowledge" prefix {
//        // all
//        case Nil JsonGet _ => Knowledge("#1:0","", "Root 1",Set.empty[Information]) : JValue
//        
//        // by id
//        case id :: Nil JsonGet _ => Knowledge(id, "#1:0", "Root 2",Set.empty[Information]) : JValue
//    })
//
//}