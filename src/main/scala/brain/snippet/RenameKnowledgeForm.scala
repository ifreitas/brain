package brain.snippet

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import net.liftweb.http.S
import net.liftweb.http.S.error
import net.liftweb.http.SHtml
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
import net.liftweb.http.js.JE

class RenameKnowledgeForm {

	var id   = ""
	var name = ""

    def render = {
        "#renameKnowledgeNameInput" #> text(name, name = _) &
        "name=id"          			#> hidden(id=_, id, "id"->"whatToRename") &
        "type=submit"            	#> ajaxOnSubmit(()=> save(name, id))
    }
	
	def save(name:String, id:String): JsCmd = {
		if (name==null || name.trim.isEmpty)
			error("renameKnowledgeModalStatus", "Please fill the name field.")
		else if(id.trim.isEmpty)
			error("renameKnowledgeModalStatus", "Did was not possible to identify the knowledge to rename. Please close this popup and try again.")
		else{
			doSave(name, id) match{
			case Some(knowledge) => return Run(s"afterRenameNewKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("renameKnowledgeModalStatus", "Invalid knowledge name.");
			}
		}
	}

    def doSave(knowledgeName: String, id: String): Option[Vertex] = {
        val db: OrientGraph = GraphDb.get
        try {
            var knowledge = db.getVertex(id)
            knowledge.setProperty("name", knowledgeName)
            db.commit()
            return Some(knowledge)
        }
        catch {
            case t: Throwable => db.rollback(); println(t.getCause()); return None
        }
        finally {
            if (db != null) db.rollback(); db.shutdown
        }
    }
}