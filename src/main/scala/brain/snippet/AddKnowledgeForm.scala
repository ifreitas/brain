package brain.snippet

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import net.liftweb.http.S.error
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.SHtml.ajaxOnSubmit
import net.liftweb.http.SHtml.hidden
import net.liftweb.http.SHtml.text
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.Helpers.strToSuperArrowAssoc
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.filter.OSQLPredicate
import com.orientechnologies.orient.core.db.record.OIdentifiable
import com.orientechnologies.orient.core.command.OCommand
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.impls.orient.OrientVertex

class AddKnowledgeForm {
	var name = ""
	var parentId = ""
	    
    def render = {
        "#addKnowledgeNameInput" #> text(name, name = _) & 
        "name=parentId"          #> hidden(parentId = _, parentId, ("id"->"into")) &
        "type=submit"            #> ajaxOnSubmit(()=>save(name, parentId))
    }
	
	def save(name:String, parentId:String): JsCmd = {
		if (name==null || name.trim.isEmpty)
			error("addKnowledgeModalStatus", "Please fill the name field.")
		else if(parentId.trim.isEmpty)
			error("addKnowledgeModalStatus", "Did was not possible to identify the parent knowledge. Please close this popup and try again.")
		else{
			doSave(name, parentId) match{
			case Some(knowledge) => return Run(s"afterAddNewKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("addKnowledgeModalStatus", "Invalid knowledge name.");//Noop
			}
		}
	}

    def doSave(knowledgeName: String, parentId: String): Option[Vertex] = {
        val db: OrientGraph = GraphDb.get
        try {
            val knowledgeParent = db.getVertex(parentId)
            val newKnowledge    = db.addVertex("class:Knowledge", "name", knowledgeName)
            db.addEdge("class:Include", knowledgeParent, newKnowledge, "include")
            db.commit()
            return Some(newKnowledge)
        }
        catch {
            case t: Throwable => db.rollback(); println(t.getCause()); return None
        }
        finally {
            if (db != null) db.rollback(); db.shutdown
        }
    }
}