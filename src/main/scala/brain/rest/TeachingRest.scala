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

object TeachingRest extends RestHelper{
    
    implicit private def teachingToJValue(teaching:Teaching):JValue=decompose(teaching)
    
//    serve("rest"/"informations" prefix {
//        case informationId :: teachings :: Nil JsonGet _ => Teaching("#3:0", Set("what"), "hey", Set(":*")) : JValue
//        case informationId :: "teachings" :: teachingId :: Nil JsonGet _ => Teaching("#3:1", Set("what"), "hey", Set(":*")) : JValue
//    })

}