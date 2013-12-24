/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Israel Freitas -- ( gmail => israel.araujo.freitas)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
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
import scala.xml.NodeSeq

class CreateKnowledgeForm {
	var name = ""
	var parentId = ""
	    
    def render = {
        "#createKnowledgeNameInput" #> text(name, name = _) & 
        "name=parentId"          #> hidden(parentId = _, parentId, ("id"->"into")) &
        "type=submit"            #> ajaxOnSubmit(()=>create(name, parentId))
    }
	
	def create(name:String, parentId:String): JsCmd = {
		if (name==null || name.trim.isEmpty)
			error("createKnowledgeFormStatus", "Please fill the name field.")
		else if(parentId.trim.isEmpty)
			error("createKnowledgeFormStatus", "Unable to identify the parent knowledge. Please close this popup and try again.")
		else{
			doCreate(name, parentId) match{
			case Some(knowledge) => return Run(s"afterCreateKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("createKnowledgeFormStatus", "Invalid knowledge name.");//Noop
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
            if (db != null) db.shutdown
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
			error("updateKnowledgeFormStatus", "Please fill the name field.")
		else if(id.trim.isEmpty)
			error("updateKnowledgeFormStatus", "Unable to identify the knowledge to rename. Please close this popup and try again.")
		else{
			doUpdate(name, id) match{
			case Some(knowledge) => return Run(s"afterUpdateKnowledge({id:'${knowledge.getId}', name:'$name', data:{}, children:[]})")
			case _ => error("updateKnowledgeFormStatus", "Invalid knowledge name.");
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
            if (db != null) db.shutdown
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
			error("deleteKnowledgeFormStatus", "Unable to identify the knowledge to delete. Please close this popup and try again.")
		else{
			doDelete(id) match{
			case Some(knowledge) => return Run(raw"afterDeleteKnowledge({id:'${knowledge.getId}', name:'${knowledge.getProperty("name")}', data:{}, children:[]})")
			case _ => error("deleteKnowledgeFormStatus", "Invalid knowledge. Unable to continue.");
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
            if (db != null) db.shutdown
        }
    }
    
    def deleteCascade(knowledge:Vertex)(implicit db:OrientGraph):Unit={
        val children = knowledge.getVertices(Direction.OUT, "Include")
        val topics   = knowledge.getVertices(Direction.OUT, "Division")
        
        //topics.foreach(deleteTopic) // TODO
        children.foreach(deleteCascade)
        db.removeVertex(knowledge)
    }
}

