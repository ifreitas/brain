package brain.config

import java.util.Properties
import net.liftweb.util.Props

object Config {
    private var graphDbUri:String      = ""
    private var graphDbUser:String     = ""
    private var graphDbPassword:String = ""

    def load(): Unit = {
        this.graphDbUri      = Props.get("graphdb.uri", "")
        this.graphDbUser     = Props.get("graphdb.user", "")
        this.graphDbPassword = Props.get("graphdb.password", "")
    }

    def getGraphDbUri      = { graphDbUri      }
    def getGraphDbUser     = { graphDbUser     }
    def getGraphDbPassword = { graphDbPassword }
}