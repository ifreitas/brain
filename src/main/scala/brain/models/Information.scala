package brain.models

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import net.liftweb.http.S.error
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Direction
import scala.xml.NodeSeq
import net.liftweb.json.JValue
import net.liftweb.http.js.JsExp
import net.liftweb.json.JString
import net.liftweb.http.js.JsCmd.unitToJsCmd


case class Information(val id:String, val name:String, val knowledgeId:String){
    def toJson:String={
        raw"{id:'$id', name:'$name', knowledgeId:'$knowledgeId'}"
    }
}

