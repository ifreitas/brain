package bootstrap.liftweb

import java.util.Locale

import scala.collection.JavaConverters.mapAsJavaMapConverter

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.metadata.schema.OType
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx

import brain.config.Config
import brain.db.GraphDb
import net.liftmodules.FoBo
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.http.Html5Properties
import net.liftweb.http.LiftRules
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http.Req
import net.liftweb.http.SessionVar
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.util.Vendor.valToVendor

// Inspirado em: http://stackoverflow.com/questions/8305586/where-should-my-sessionvar-object-be
object appSession extends SessionVar[Map[String, Any]](Map()) {
    val LocaleKey = "locale"
    val UserKey = "user"
}

class Boot {

    def boot = {

        // Where find snippet and comet
        LiftRules.addToPackages("brain")

        // Full support to Html5
        LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

        // i18N
        LiftRules.localeCalculator = localeCalculator _
        LiftRules.resourceNames = "i18n/messages" :: LiftRules.resourceNames
        LiftRules.resourceNames = "props" :: LiftRules.resourceNames

        //Show the spinny image when an Ajax call starts
        LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

        // Make the spinny image go away when it ends
        LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

        // Brain Config Object
        Config.load

        createDbUnlessAlreadyExists
        
    }

    def createDbUnlessAlreadyExists = {
        val orientServerAdmin = new OServerAdmin("remote:localhost")
        orientServerAdmin.connect(Config.getGraphDbUser, Config.getGraphDbPassword)
        try {
            if(!orientServerAdmin.listDatabases().keySet().contains(Config.getGraphDbName)){
            	orientServerAdmin.createDatabase(Config.getGraphDbName, "graph", "plocal")
            	createSchema
                createRootVertexAndConf
            }
        }
        catch{
            case t :Throwable=> {
                orientServerAdmin.dropDatabase("plocal")
                throw new Exception("Was not possible to create the database. Cause: " + t.getCause())
            }
        }
        finally {
            if(orientServerAdmin.isConnected()) orientServerAdmin.close(false)
        }
    }
    
    def createRootVertexAndConf{
        val db = GraphDb.get
        try{
        	val root = db.addVertex("class:Knowledge", Map[String, Object]("name"->"Root").asJava)
        	db.commit // https://github.com/orientechnologies/orientdb/wiki/Transactions#optimistic-transaction
        	db.addVertex("class:Conf", Map[String, Object]("rootId"->root.getId().toString(), "defaultDepthTraverse"->new Integer(3)).asJava)
   			db.commit
        }
        catch {
		  case t : Throwable => db.rollback; throw new Exception("Was not possible to create the root node. Cause: " + t.getCause())
		}
        finally {
        	if(db != null && !db.isClosed()) db.shutdown()
        }
    }
    
    def createSchema(){
        val db:OrientGraphNoTx = GraphDb.getNoTx
        try {
        	db.createEdgeType("Include")
        	db.createEdgeType("Division")
        	db.createVertexType("Conf")
        	db.createVertexType("Knowledge").createProperty("name", OType.STRING).setMandatory(true).setMin("2").setMax("40")
        	db.createVertexType("Information").createProperty("name", OType.STRING).setMandatory(true).setMin("2").setMax("40")
        }
        catch {
		  case t : Throwable => {
		       db.drop
		       throw new Exception("Was not possible to create the databse schema. Cause: " + t.getCause())
		  }
		}
        finally {
        	if(db != null && !db.isClosed()) db.shutdown()
        }
    }

    def localeCalculator(request: Box[HTTPRequest]): Locale = {
        def calcLocale: Locale = {
            val locale = LiftRules.defaultLocaleCalculator(request)
            appSession.set(Map(appSession.LocaleKey -> locale))
            locale
        }

        appSession.is.get(appSession.LocaleKey) match {
            case Some(l: Locale) => l
            case _               => calcLocale
        }
    }
}