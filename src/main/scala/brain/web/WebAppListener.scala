package brain.web

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import brain.db.OrientDbServer

class WebAppListener extends ServletContextListener {
	
	def contextInitialized(servletContextEvent:ServletContextEvent) {
	    //System.setProperty("run.mode", "production")
	    OrientDbServer.start
	}

	def contextDestroyed(servletContextEvent:ServletContextEvent) {
	    OrientDbServer.stop
	}
  
}
