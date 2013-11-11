

package brain.functionaltest

object RunWebApp extends App {
//  val server = new Server
//  val scc = new SelectChannelConnector
//  scc.setPort(8080)
//  server.setConnectors(Array(scc))
//
//  val context = new WebAppContext()
//  context.setServer(server)
//  context.setWar("src/main/webapp")
//
//  val context0: ContextHandler = new ContextHandler();
//  context0.setHandler(context)
//  server.setHandler(context0)
//
//  try {
//    println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP")
//    server.start()
//    while (System.in.available() == 0) {
//      Thread.sleep(5000)
//    }
//    server.stop()
//    server.join()
//  } catch {
//    case exc: Exception => {
//      exc.printStackTrace()
//      System.exit(100)
//    }
//  }
    

  try {
    println(">>> PRESS ANY KEY TO STOP")
    JettyServer.start
    while (System.in.available() == 0) {
      Thread.sleep(5000)
    }
    JettyServer.stop
    JettyServer.join
  } catch {
    case exc: Exception => {
      exc.printStackTrace()
      System.exit(100)
    }
  }
}
