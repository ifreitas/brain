package brain.snippet

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import net.liftweb.http.S.error
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.http.JsContext
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.Helpers.strToSuperArrowAssoc
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.filter.OSQLPredicate
import com.orientechnologies.orient.core.db.record.OIdentifiable
import com.orientechnologies.orient.core.command.OCommand
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import com.tinkerpop.blueprints.Direction
import net.liftweb.common.Empty
import scala.xml.NodeSeq
import scala.xml.Elem
import java.util.Date
import net.liftweb.json.JValue
import net.liftweb.http.js.JsExp
import net.liftweb.json.JField
import net.liftweb.json.JField
import net.liftweb.json.JString
import brain.models.Information
import net.liftweb.common.Full
import com.tinkerpop.blueprints.Graph


class InformationSnippet {
	var id   = ""
	var name = ""
	var knowledgeId = ""
	    
//    def render = {
//        "#informationNameInput"   #> text(name, name = _) & 
//        "#informationId"          #> hidden(id = _, id) &
//        "#informationKnowledgeId" #> hidden(knowledgeId = _, knowledgeId) &
//        "type=submit"             #> ajaxOnSubmit(()=>save(Information(id, name, knowledgeId)))
//    }
	
//	def save(information:Information): JsCmd = {
//		if (information.name.trim.isEmpty)
//			error("informationFormStatus", "Please fill the name field.")
//		else if(information.knowledgeId.trim.isEmpty)
//			error("informationFormStatus", "Unable to identify the knowledge owner of this information. Please close this popup and try again.")
//		else{
//		    if(information.id.trim().isEmpty()) create(information) else update(information)
//		}
//	}
	
//	def create(info:Information):JsCmd={
//	    doCreate(info) match{
//			case Some(information) => return Run(s"afterCreateInformation(${information.toJson})")
//			case _ => error("informationFormStatus", "Invalid name.");
//		}
//	}
//	def update(info:Information):JsCmd={
//	    doUpdate(info) match{
//			case Some(information) => return Run(s"afterUpdateInformation(${information.toJson})")
//			case _ => error("informationFormStatus", "Invalid name.");
//		}
//	}
	
//	def doCreate(information:Information):Option[Information]={
//        val db: OrientGraph = GraphDb.get
//        try {
////        	val newInformation = db.addVertex("class:Information", "name", information.name)
//			val newInformation = db.addVertex(None, "name", information.name)
//            val knowledge      = db.getVertex(information.knowledgeId)
//            
//            db.addEdge(None, knowledge, newInformation, "division")
//            db.commit()
//            return Some(Information(newInformation.getId().toString(), information.name, information.knowledgeId))
//        }
//        catch {
//            case t: Throwable => db.rollback(); println(t.getMessage()); return None
//        }
//        finally {
//            if (db != null) db.shutdown
//        }	    
//	}
	def doUpdate(information:Information):Option[Information]={
	    val db: OrientGraph = GraphDb.get
        try {
            var oldInformation = db.getVertex(id)
            oldInformation.setProperty("name", information.name)
            db.commit()
            return Some(information)
        }
        catch {
            case t: Throwable => db.rollback(); println(t.getCause()); return None
        }
        finally {
            if (db != null) db.shutdown
        }
	}

}

class DeleteInformationForm {
	var id = ""
	    
//    def render = {
//        "#whatInformationToDelete" #> hidden(id = _, id) &
//        "type=submit"    #> ajaxOnSubmit(()=>delete(id))
//    }
	
//	def delete(id:String): JsCmd = {
//		if(id.trim.isEmpty)
//			error("deleteInformationFormStatus", "Unable to identify the information to delete. Please close this popup and try again.")
//		else{
//			doDelete(id) match{
//			case Some(information) => return Run(raw"afterDeleteInformation(${information.toJson})")
//			case _ => error("deleteInformationFormStatus", "Invalid information. Unable to continue.");
//			}
//		}
//	}

//    def doDelete(id: String): Option[Information] = {
//        implicit val db: OrientGraph = GraphDb.get
//        try {
//            
//            val sqlString = raw"select from (traverse out() from  $id)";
//            val vertices:java.lang.Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute();
//            val informationVextex = vertices.head
//            val information = Some(Information(informationVextex.getId().toString(), informationVextex.getProperty("name"), ""))
//            //informationVextex.getVertices(Direction.OUT, "Include").foreach(db.removeVertex)
//            db.removeVertex(informationVextex)
//            
//            db.commit()
//            return information
//        }
//        catch {
//            case t: Throwable => db.rollback(); println(t.getMessage()); return None
//        }
//        finally {
//            if (db != null) db.shutdown
//        }
//    }
}

object InformationSnippet{
    def loadInformationsMethod:NodeSeq = {
        def getInformations(knowledgeId:String):JsCmd = {
        	val db: Graph = GraphDb.get;
//        	try {
//        	    val vertices:java.lang.Iterable[Vertex] = db.query().has("_class_", Information.persistentName).has("in_division", knowledgeId).vertices()
//	            val informationsJson = vertices.map(v=>Information(v.getId().toString(), v.getProperty("name"), knowledgeId).toJson)
//	            return new JsCmd {
//	            	def toJsCmd = raw"[${informationsJson.mkString(",")}]"
//	            }
//	        }
//	        //catch {
//	        //    case t: Throwable => db.rollback(); println(t.getMessage());
//	        //}
//	        finally {
//	            if (db != null) db.shutdown
//	        }
            
        }
        
        <script>
			<![CDATA[
			function loadInformations(){
				if(ObjectManager.lastClicked != null){
        		    ]]>{jsonCall(
        		            		new JsExp { def toJsCmd = "{id:ObjectManager.lastClicked.id.toString()}"},
        		            		new JsContext(Full("afterReceiveInformation"), Empty),
        		            		(json:JValue)=>{val JString(s) = (json \ "id"); getInformations( s.toString() )} )
        		       }<![CDATA[;
        		}
			}
        	]]>
        </script>
    }
    
}