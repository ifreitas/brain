package brain.snippet

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import net.liftweb.http.S.error
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmds.Alert
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

class CreateKnowledgeForm {
	var name = ""
	var parentId = ""
	var teste = ""
	    
    def render = {
        "#createKnowledgeNameInput" #> text(name, name = _) & 
        "name=parentId"          #> hidden(parentId = _, parentId, ("id"->"into")) &
        "type=submit"            #> ajaxOnSubmit(()=>create(name, parentId))
    }
	
	def create(name:String, parentId:String): JsCmd = {
		if (name==null || name.trim.isEmpty)
			error("createKnowledgeWindowStatus", "Please fill the name field.")
		else if(parentId.trim.isEmpty)
			error("createKnowledgeWindowStatus", "Did was not possible to identify the parent knowledge. Please close this popup and try again.")
		else{
			doCreate(name, parentId) match{
			case Some(knowledge) => return Run(s"afterCreateKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("createKnowledgeWindowStatus", "Invalid knowledge name.");//Noop
			}
		}
	}

    def doCreate(knowledgeName: String, parentId: String): Option[Vertex] = {
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

class UpdateKnowledgeForm {
	var id   = ""
	var name = ""

    def render = {
        "#updateKnowledgeNameInput" #> text(name, name = _) &
        "name=id"          			#> hidden(id=_, id, "id"->"whatToRename") &
        "type=submit"            	#> ajaxOnSubmit(()=> update(name, id))
    }
	
	def update(name:String, id:String): JsCmd = {
		if (name==null || name.trim.isEmpty)
			error("updateKnowledgeWindowStatus", "Please fill the name field.")
		else if(id.trim.isEmpty)
			error("updateKnowledgeWindowStatus", "Did was not possible to identify the knowledge to rename. Please close this popup and try again.")
		else{
			doUpdate(name, id) match{
			case Some(knowledge) => return Run(s"afterUpdateKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("updateKnowledgeWindowStatus", "Invalid knowledge name.");
			}
		}
	}

    def doUpdate(knowledgeName: String, id: String): Option[Vertex] = {
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

class DeleteKnowledgeForm {
	var id = ""
	    
    def render = {
        "name=id"      #> hidden(id = _, id, ("id"->"whatKnowledgeToDelete")) &
        "type=submit"  #> ajaxOnSubmit(()=>delete(id))
    }
	
	def delete(id:String): JsCmd = {
		if(id.trim.isEmpty)
			error("deleteKnowledgeWindowStatus", "Was not possible to identify the parent knowledge. Please close this popup and try again.")
		else{
			doDelete(id) match{
			case Some(knowledge) => return Run(raw"afterDeleteKnowledge({id:'${knowledge.getId}', name:'${knowledge.getProperty("name")}', data:{}, children:[]})")
			case _ => error("deleteKnowledgeWindowStatus", "Invalid knowledge name.");
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
