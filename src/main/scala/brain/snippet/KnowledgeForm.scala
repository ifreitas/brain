package brain.snippet

import net.liftweb._
import http._
import common._
import util.Helpers._
import js._
import JsCmds._
import JE._
import S._
import SHtml._
import brain.db.GraphDb
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.Graph
import net.liftweb.http.js.jquery.JqJsCmds.ModalDialog

object KnowledgeForm {

    def render = {
        
        val nameFieldId = "addKnowledgeNameInput"
        var name = ""
        var id = 0

        def save(): JsCmd = {
            if (name.trim().isEmpty){
                error("addKnowledgeModalStatus", "Please fill the name field.");Noop
            }
            else{
                doSave(name) match{
                    case Some(knowledge) => return new Run("afterAddNewKnowledge({id:'" + knowledge.getId + "', name:'"+name+"', data:{}, children:[]})")
                    case _ => error("addKnowledgeModalStatus", "Invalid knowledge name.");Noop
                }
            }
        }
        
        def doSave(name:String) : Option[Vertex] = {
   			var db:Graph = null
        	var knowledge:Vertex =  null
        	try{
        	    db = GraphDb.get
        	    knowledge = db.addVertex("class:Knowledge")
        	    knowledge.setProperty("name", name)
        	    db
        	}
   			catch {
			  case t: Throwable=> return None
			}
        	finally {
        		if(db != null) db.shutdown
        	}
        	Some(knowledge)
        }
        

        ("#"+nameFieldId) #> text(name, name = _) &
        "type=submit" #> ajaxOnSubmit(save)
    }
}