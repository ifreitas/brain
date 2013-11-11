package brain.functionaltest

import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

object JettyServer {
  private val server = new Server
  private val scc = new SelectChannelConnector
  scc.setPort(8080)
  server.setConnectors(Array(scc))

  private val context = new WebAppContext()
  context.setServer(server)
  context.setWar("src/main/webapp")

  private val context0: ContextHandler = new ContextHandler();
  context0.setHandler(context)
  server.setHandler(context0)
  
  def start= { println("STARTING EMBEDDED JETTY SERVER");this.server.start()}
  def stop = { println("STOPPING EMBEDDED JETTY SERVER");this.server.stop()}
  def join = { this.server.join()  }
  def isRunning = { this.server.isRunning() }
  
}
