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

import brain.db.OrientDbServer

object RunWebApp extends App {
    try {
        OrientDbServer.start

        println(">>> PRESS ANY KEY TO STOP")
        JettyServer.start
        while (System.in.available() == 0) {
            Thread.sleep(5000)
        }
        OrientDbServer.stop
        JettyServer.stop
        JettyServer.join
    }
    catch {
        case exc: Exception => {
            exc.printStackTrace()
            System.exit(100)
        }
    }
}
