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
import java.io.File

object Config {
    private var graphDbUser: String     = ""
    private var graphDbPassword: String = ""
    private var graphDbName: String     = ""
    private var graphDbType: String     = ""
    private var knowledgeBaseDir:File   = null

    def load(): Unit = {
        createDirUnlessAlreadyExists(getBrainDataPath)
        createDirUnlessAlreadyExists(getGraphDatabasePath)
        createDirUnlessAlreadyExists(getKnowledgeBasePath)
        
        this.knowledgeBaseDir = new File(getKnowledgeBasePath)
        
        this.graphDbType     = Props.get("graphdb.type", "")
        this.graphDbName     = Props.get("graphdb.name", "")
        this.graphDbUser     = Props.get("graphdb.user", "")
        this.graphDbPassword = Props.get("graphdb.password", "")
    }
    
    def createDirUnlessAlreadyExists(dirName:String):Unit = {
        var dir = new File(dirName)
        if(! dir.exists()){
            println(s"Creating the directory '$dir'.")
            dir.mkdir
        }
    }

    def getGraphDbUri      = { s"$graphDbType:$getGraphDatabasePath/$graphDbName" }
    def getGraphDbUser     = { graphDbUser }
    def getGraphDbPassword = { graphDbPassword }
    def getGraphDbType     = { graphDbType }
    def getGraphDbName     = { graphDbName }
    
    def getBrainDataPath     = System.getProperty("user.home")+"/brain"
    def getGraphDatabasePath = getBrainDataPath+"/databases"
    def getKnowledgeBasePath = getBrainDataPath+"/knowledge_base"
    def getKnowledgeBaseDir  = knowledgeBaseDir
}