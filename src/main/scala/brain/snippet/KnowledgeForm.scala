package brain.snippet

import net.liftweb._
import http._
import common._
import util.Helpers._
import js._
import JsCmds._
import JE._
import S._
import SHtml._

object KnowledgeForm {

    def render = {
        val nameFieldId = "addKnowledgeNameInput"
        var name = "";

        def process(): JsCmd = {
            // try to save the name, so...
            if (name.isEmpty){error("status", "Please fill the name field.");Noop}
            else{
            	SetValById(nameFieldId, "")
            	new Run("$('#addKnowledgeModal').modal('hide');Log.write(\"Knowledege named '" + name + "' added succefully.\");")
            }
        }

        ("#"+nameFieldId) #> text(name, name = _) &
        "type=submit" #> ajaxOnSubmit(process)
//        "type=submit" #> ajaxSubmit("Enviar",()=>process)
    }
}