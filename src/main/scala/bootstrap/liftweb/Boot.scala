package bootstrap.liftweb

import java.util.Locale
import net.liftmodules.FoBo
import net.liftweb.common.Box
import net.liftweb.http.Html5Properties
import net.liftweb.http.LiftRules
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http.Req
import net.liftweb.http.SessionVar
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.util.Helpers.strToSuperArrowAssoc
import net.liftweb.util.Vendor.valToVender
import brain.config.Config
import net.liftweb.util.Props
import net.liftweb.http.js.extcore.ExtCoreArtifacts

// Inspirado em: http://stackoverflow.com/questions/8305586/where-should-my-sessionvar-object-be
object appSession extends SessionVar[Map[String, Any]](Map()){
    val LocaleKey = "locale"
    val UserKey   = "user"
}

class Boot {

    def boot = {

        // Where find snippet and comet
        LiftRules.addToPackages("bora")

        // Full support to Html5
        LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
        
        // i18N
        LiftRules.localeCalculator = localeCalculator _
        LiftRules.resourceNames = "i18n/messages" :: LiftRules.resourceNames
        
        // FoBo Module
        FoBo.InitParam.JQuery = FoBo.JQuery191
        FoBo.InitParam.ToolKit = FoBo.Bootstrap231
        FoBo.InitParam.ToolKit = FoBo.FontAwesome300
        FoBo.init
        
        // Bora Config Object
        Config.load
    }

    def localeCalculator(request: Box[HTTPRequest]): Locale = {
        def calcLocale: Locale = {
            val locale = LiftRules.defaultLocaleCalculator(request)
            appSession.set(Map(appSession.LocaleKey -> locale))
            locale
        }
        
        appSession.is.get(appSession.LocaleKey) match {
          case Some(l: Locale) => l
          case _ => calcLocale
	    }
    }
}