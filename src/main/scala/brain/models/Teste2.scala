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
package brain.models

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.ansvia.graph.BlueprintsWrapper._
import brain.db.GraphDb
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import scala.collection.JavaConversions.iterableAsScalaIterable
import com.tinkerpop.blueprints.Vertex
import scala.xml.Text
import brain.db.OrientDbServer
import com.orientechnologies.orient.client.remote.OServerAdmin
import brain.config.Config
import java.io.File

object Teste2 {

    def main(args: Array[String]): Unit = {
//        implicit val db:Graph = GraphDb.get
        try{
            OrientDbServer.start
            
            implicit val db = new OrientGraph("plocal:/Users/israelfreitas/Documents/workspace/brain/databases/brain_dev")
            
            println(Knowledge.findAll.size)
            
            db.shutdown()
            
            OrientDbServer.stop
        }
        finally{
//            db.shutdown()
        }
    }

}