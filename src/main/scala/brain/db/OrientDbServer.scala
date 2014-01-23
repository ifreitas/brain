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
package brain.db

import com.orientechnologies.orient.server.OServerMain
import com.orientechnologies.orient.server.OServer
import java.io.File
import brain.config.Config

object OrientDbServer {
	def start:Unit = {
	    try{
	        if(System.getProperty("ORIENTDB_HOME")==null){
	        	System.setProperty("ORIENTDB_HOME", Config.getBrainDataPath)
	        }
	        println("Trying to start the database. ORIENTDB_HOME: "+System.getProperty("ORIENTDB_HOME"))
	        serverInstance.activate()
	    	println("The Database is running now.")
	    }
	    catch{
	        case e:Throwable => throw new Exception("Unable to start the Database.", e)
	    }
	}
	def stop:Unit = {
		try{
	    	serverInstance.shutdown
	    	println("The Database is stopped now.")
	    }
	    catch{
	        case e:Throwable => throw new Exception("Unable to stop the Database.", e)
	    }
	}
	def getInstance = serverInstance
    lazy private val serverInstance:OServer = OServerMain.create.startup(dbConfig)
    lazy private val dbConfig = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
              <orient-server>
                  <network>
                      <protocols>
                        <protocol name="binary" implementation="com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary"/>
                      </protocols>
                      <listeners>
                          <listener ip-address="0.0.0.0" port-range="2424-2430" protocol="binary"/>
                      </listeners>
                  </network>
                  <users>
    				<user name="root" password="826C8F3C84160540746D5D1A3CD24C701F7C9AEFDA80EEF3D40440190E3DFF95" resources="*"/>
                  </users>
                  <properties>
                      <entry name="server.cache.staticResources" value="false"/>
                      <entry name="log.console.level" value="info"/>
                      <entry name="log.file.level" value="fine"/>
                      <!-- The following is required to eliminate an error or warning "Error on resolving property: ORIENTDB_HOME" -->
                      <entry name="plugin.dynamic" value="false"/>
                  </properties>
              </orient-server>
        """
 
}