package bootstrap.liftweb

import java.util.Locale
import com.orientechnologies.orient.client.remote.OServerAdmin
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import brain.config.Config
import net.liftmodules.FoBo
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.http.Html5Properties
import net.liftweb.http.LiftRules
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http.Req
import net.liftweb.http.SessionVar
import net.liftweb.http.provider.HTTPRequest
import com.orientechnologies.orient.core.metadata.schema.OType

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

        // FoBo Module
        FoBo.InitParam.JQuery = FoBo.JQuery191
        FoBo.InitParam.ToolKit = FoBo.Bootstrap231
        FoBo.InitParam.ToolKit = FoBo.FontAwesome300
        FoBo.init

        //Show the spinny image when an Ajax call starts
        LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

        // Make the spinny image go away when it ends
        LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

        // Brain Config Object
        Config.load

        createDbUnlessAlreadyExists
    }

    def createDbUnlessAlreadyExists = {
        var db:OrientGraph = null
        val orientServerAdmin = new OServerAdmin("remote:localhost")
        orientServerAdmin.connect(Config.getGraphDbUser, Config.getGraphDbPassword)
        try {
            if(!orientServerAdmin.listDatabases().keySet().contains(Config.getGraphDbName)){
            	orientServerAdmin.createDatabase(Config.getGraphDbName, "graph", "plocal")
                db = brain.db.GraphDb.get.asInstanceOf[OrientGraph]
            	createSchema(db)
            }
        }
        catch{
            case t :Throwable=> println("Db already exists.")
        }
        finally {
            if(orientServerAdmin.isConnected()) orientServerAdmin.close(false)
            if(db != null && !db.isClosed()) db.shutdown()
        }
    }
    
    def createSchema(db:OrientGraph){
        val knowledgeVertex = db.createVertexType("Knowledge")
        knowledgeVertex.createProperty("name", OType.STRING).setMandatory(true).setMin("2").setMax("40")
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