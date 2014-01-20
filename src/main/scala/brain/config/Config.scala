/**
 * Copyright 2013 Israel Freitas (israel.araujo.freitas@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    def getGraphDbUri      = { graphDbType + ":" + System.getProperty("ORIENTDB_HOME") +"/databases/"+ graphDbName }
    def getGraphDbUser     = { graphDbUser }
    def getGraphDbPassword = { graphDbPassword }
    def getGraphDbType     = { graphDbType }
    def getGraphDbDir      = { graphDbDir }
    def getGraphDbName     = { graphDbName }
}