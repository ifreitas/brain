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
package brain.web

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import brain.db.OrientDbServer
import brain.models.ProgramD
import java.net.URL
import org.aitools.programd.Core
import java.net.MalformedURLException
import org.aitools.util.resource.URLTools
import brain.config.Config

class WebAppListener extends ServletContextListener {
	
	def contextInitialized(servletContextEvent:ServletContextEvent) {
	    //System.setProperty("run.mode", "production")
	    OrientDbServer.start
	    
	    ProgramD.prepare
		val theURL = getBaseURL(servletContextEvent);
	    println("CORE URL"+ theURL.toString())
		val core = new Core(theURL, URLTools.contextualize(theURL, Config.getProgramDConfPath));
		ProgramD.start(core);
	}

	def contextDestroyed(servletContextEvent:ServletContextEvent) {
	    ProgramD.shutdown
	    OrientDbServer.stop
	}
	
	def getBaseURL(servletContextEvent:ServletContextEvent):URL = {
		try {
		  return servletContextEvent.getServletContext().getResource("/");
		}
		catch {
		    case e:MalformedURLException => {
		        servletContextEvent.getServletContext().log("Unable to get the base url.", e);
		    }
			
		  return null;
		}
	}
  
}
