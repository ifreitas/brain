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

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.xml.NodeSeq

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph

import brain.db.GraphDb
import net.liftweb.common.Empty
import net.liftweb.common.Full
import net.liftweb.http.JsContext
import net.liftweb.http.S.error
import net.liftweb.http.SHtml.ajaxOnSubmit
import net.liftweb.http.SHtml.hidden
import net.liftweb.http.SHtml.jsonCall
import net.liftweb.http.SHtml.text
import net.liftweb.http.SHtml.textarea
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsExp
import net.liftweb.json.JString
import net.liftweb.json.JValue
import net.liftweb.util.Helpers.strToCssBindPromoter
import brain.models.Teaching

class TeachingSnippet {
	var id              = ""
	var whenTheUserSays = ""
	var respondingTo    = ""
	var memorize        = ""
	var say             = ""
	var informationId   = ""
	    
    def render = {
        "#whenTheUserSaysInput"	#> textarea(whenTheUserSays, whenTheUserSays = _) & 
        "#respondingToInput"   	#> text(respondingTo, respondingTo = _) & 
        "#memorizeInput"   		#> textarea(memorize, memorize = _) & 
        "#sayInput"   			#> textarea(say, say = _) & 
        "#teachingInformationId" 	#> hidden(informationId = _, informationId) &
        "#teachingId"     		#> hidden(id = _, id) &
        "type=submit"   		#> ajaxOnSubmit(()=>save(Teaching(id, informationId, whenTheUserSays, respondingTo, memorize, say)))
    }
	
	def save(teaching:Teaching): JsCmd = {
	    try{
	        if(teaching.id.trim().isEmpty()) create(teaching) else update(teaching)
	    }
	    catch{
	        case t : Throwable => t.printStackTrace(); error("teachingFormStatus", t.toString())
	    }
	}
	
	def create(teach:Teaching):JsCmd= Run(s"afterCreateTeaching(${doCreate(teach).get.toJson})")
	def update(teach:Teaching):JsCmd= Run(s"afterUpdateTeaching(${doUpdate(teach).get.toJson})")
	
	def doCreate(teaching:Teaching):Option[Teaching]={
        implicit val db: OrientGraph = GraphDb.get
	    try     { Teaching.create(teaching)   }
        finally { if (db != null) db.shutdown }
	}
	def doUpdate(teaching:Teaching):Option[Teaching]={
	    implicit val db: OrientGraph = GraphDb.get
	    try     { Teaching.update(teaching)   }
        finally { if (db != null) db.shutdown }
	}
}

class DeleteTeachingForm {
	var id = ""
	    
    def render = {
        "#whatTeachingToDelete" #> hidden(id = _, id) &
        "type=submit"    #> ajaxOnSubmit(()=>delete(id))
    }
	
	def delete(id:String): JsCmd = {
	    try{
	        Run(s"afterDeleteTeaching(${doDelete(id).get.toJson})")
	    }
	    catch{
	        case t : Throwable => t.printStackTrace(); error("deleteTeachingFormStatus", t.toString())
	    }
	}

	def doDelete(id: String): Option[Teaching] = {
	    implicit val db: OrientGraph = GraphDb.get
	    try     { Teaching.delete(id)         }
        finally { if (db != null) db.shutdown }
    }
}

object TeachingSnippet{
    def loadTeachingsMethod:NodeSeq = {
        def getTeachings(informationId:String):JsCmd = {
        	val db: OrientGraph = GraphDb.get;
        	try {
	            val sqlString = raw"select from Teaching where in_include = $informationId order by whenTheUserSays";
	            val vertices:java.lang.Iterable[Vertex] = db.command(new OSQLSynchQuery[Vertex](sqlString)).execute();
	            val teachingsJson = vertices.map(vertex=> {var t = Teaching(vertex.getId().toString(), "", vertex.getProperty("whenTheUserSays"), vertex.getProperty("respondingTo"), vertex.getProperty("memorize"), vertex.getProperty("say")); t.toJson})
	            return new JsCmd {
	            	def toJsCmd = raw"[${teachingsJson.mkString(",")}]"
	            }
	        }
	        finally {
	            if (db != null) db.shutdown
	        }
            
        }
        
        <script>
			<![CDATA[
			function loadTeachings(){
         	]]>{jsonCall(
	            		new JsExp { def toJsCmd = "{id:informationExtWrapper.panel.getSelectionModel().getLastSelected().data.id}"},
	            		new JsContext(Full("afterReceiveTeachings"), Empty),
	            		(json:JValue)=>{val JString(s) = (json \ "id"); getTeachings( s.toString() )} )
        		}<![CDATA[;
			}
        	]]>
        </script>
    }
    
}