package brain.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import brain.db.OrientDbServer;

public class ChatAppListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		OrientDbServer.start();
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		OrientDbServer.stop();
	}
	
}
