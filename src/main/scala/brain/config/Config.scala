package brain.config

import java.util.Properties
import net.liftweb.util.Props

object Config {
    private var graphDbUser: String     = ""
    private var graphDbPassword: String = ""
    private var graphDbName: String     = ""
    private var graphDbType: String     = ""
    private var graphDbDir: String      = ""

    def load(): Unit = {
        this.graphDbDir      = Props.get("graphdb.dir", "")
        this.graphDbType     = Props.get("graphdb.type", "")
        this.graphDbName     = Props.get("graphdb.name", "")
        this.graphDbUser     = Props.get("graphdb.user", "")
        this.graphDbPassword = Props.get("graphdb.password", "")
    }

    def getGraphDbUri      = { graphDbType + ":" + graphDbDir + graphDbName }
    def getGraphDbUser     = { graphDbUser }
    def getGraphDbPassword = { graphDbPassword }
    def getGraphDbType     = { graphDbType }
    def getGraphDbDir      = { graphDbDir }
    def getGraphDbName     = { graphDbName }
}