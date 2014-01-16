package brain.db

import com.orientechnologies.orient.server.OServerMain
import com.orientechnologies.orient.server.OServer
import java.io.File
import brain.config.Config

object OrientDbServer {
	def start:Unit = {
	    try{
	        if(System.getProperty("ORIENTDB_HOME")==null){
	        	System.setProperty("ORIENTDB_HOME", System.getProperty("user.dir"))
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