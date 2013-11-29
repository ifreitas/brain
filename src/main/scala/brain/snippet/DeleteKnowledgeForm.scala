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
import com.tinkerpop.blueprints.Direction

class DeleteKnowledgeForm {
	var id = ""
	    
    def render = {
        "name=id"      #> hidden(id = _, id, ("id"->"whatKnowledgeToDelete")) &
        "type=submit"  #> ajaxOnSubmit(()=>delete(id))
    }
	
	def delete(id:String): JsCmd = {
		if(id.trim.isEmpty)
			error("addKnowledgeModalStatus", "Did was not possible to identify the parent knowledge. Please close this popup and try again.")
		else{
			doDelete(id) match{
			case Some(knowledge) => return Run(raw"afterDeleteKnowledge({id:'${knowledge.getId}', name:'${knowledge.getProperty("name")}', data:{}, children:[]})")
			case _ => error("addKnowledgeModalStatus", "Invalid knowledge name.");
			}
		}
	}

    def doDelete(id: String): Option[Vertex] = {
        implicit val db: OrientGraph = GraphDb.get
        try {
            val sqlString = raw"select from (traverse out() from  $id)";
            val vertices:java.lang.Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute();
            val knowledge = vertices.head
            deleteCascade(knowledge)
            db.commit()
            return Some(knowledge)
        }
        catch {
            case t: Throwable => db.rollback(); println(t.getMessage()); return None
        }
        finally {
            if (db != null) db.rollback(); db.shutdown
        }
    }
    
    def deleteCascade(knowledge:Vertex)(implicit db:OrientGraph):Unit={
        val children = knowledge.getVertices(Direction.OUT, "Include")
        val topics   = knowledge.getVertices(Direction.OUT, "Divisions")
        
        //topics.foreach(deleteTopic) // TODO
        children.foreach(deleteCascade)
        db.removeVertex(knowledge)
    }
}