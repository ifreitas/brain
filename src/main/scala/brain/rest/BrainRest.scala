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


case class Knowledge(id:String)
object Knowledge{
	def findAll()(implicit db:Graph):Set[Knowledge]  = Set.empty[Knowledge]
	def findById(id:String)(implicit db:Graph):Set[Knowledge]  = Set.empty[Knowledge]
    def create(Knowledge:Knowledge)(implicit db:Graph):Knowledge = Knowledge
    def update(Knowledge:Knowledge)(implicit db:Graph):Knowledge = Knowledge
    def delete(Knowledge:Knowledge)(implicit db:Graph):Knowledge = Knowledge
    
    
    private implicit val formats = net.liftweb.json.DefaultFormats
    def apply(vertex:Vertex):Knowledge = Knowledge("")
    def apply(in:JValue):Box[Knowledge] = Helpers.tryo{in.extract[Knowledge]}
    def unapply(in:JValue):Option[Knowledge] = Some(Knowledge(""))
}

object BrainRest extends RestHelper {
    implicit private def knowledgeToJValue(knowledge:Knowledge):JValue=decompose(knowledge)
    implicit private def knowledgeSetToJValue(knowledges:Set[Knowledge]):JValue=decompose(knowledges)
    
    serve("rest"/"knowledges" prefix{
        case Nil JsonGet _ => {
            implicit val db = GraphDb.get
            transaction{
                Knowledge.findAll : JValue
            }
        }
        
        case Nil JsonPut Knowledge(knowledge) -> _ => {
            implicit val db = GraphDb.get
            transaction{
            	Knowledge.create(knowledge)
            }
        }
    })
    
    private implicit def transaction(func: =>JValue)(implicit db:OrientGraph):JValue={
        try {
        	val result = func
        	db.commit()
        	return result
        }
        //catch{
        //	db.rollback(); retornar o correto status 500 com a correta mensagem de erro.
        //}
        finally { if( !db.isClosed() ) db.shutdown() }
    }
}