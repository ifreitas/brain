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
  
  def start= { println("STARTING EMBEDDED JETTY SERVER");this.server.start(); println("EMBEDDED JETTY SERVER STARTED");}
  def stop = { println("STOPPING EMBEDDED JETTY SERVER");this.server.stop(); println("EMBEDDED JETTY SERVER STOPPED");}
  def join = { this.server.join()  }
  def isRunning = { this.server.isRunning() }
  
}
