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
import brain.db.OrientDbServer
import com.orientechnologies.orient.client.remote.OServerAdmin
import brain.config.Config
import java.io.File
import aimltoxml.aiml.TemplateElement
import aimltoxml.aiml.Text
import aimltoxml.aiml.Get

object Teste2 {

    def main(args: Array[String]): Unit = {
        /*
         * val regex = """\$\{([a-z0-9]*)\}""".r
         * val matches = regex.findAllIn(phrase)
         * val split   = phrase.split(regex.toString)
         * val resultado = new scala.collection.mutable.ListBuffer[String]
         * for(i <- 0 to (split.size-1) ) resultado += s"${split(i)}${variaveis(i)}"
         */
        
        var list = List(1,2,3)
        
        val x = "9+10+10+1"
            // hi ${ name }
    		// hi ${name}
    		// hi $name
        //val phrase = "olá, ${nome}, ${idade}" //  \$\{([a-z]*)\}
		//val phrase = "olá, ${nome} ${idade}"  //  \$\{([a-z]*)\}
		//val phrase = "olá, ${nome}${idade}"   //  \$\{([a-z]*)\}
        "olá, $nome, $idade" //  \$\{([a-z]*)\}
		"olá, $nome $idade" //  \$\{([a-z]*)\}
		"olá, $nome$idade" //  \$\{([a-z]*)\}
        Set(Text("olá, "), new Get("nome"))
    }
//    def main(args: Array[String]): Unit = {
//    		OrientDbServer.start
//    		
//    		implicit val db = new OrientGraph("plocal:/Users/israelfreitas/Documents/workspace/brain/databases/brain_dev")
//    		
//    		val knowledges = Knowledge.findAll
//    		println(knowledges.size)
//    		println(knowledges.head.name)
//    		println(knowledges.head.getChildren.size)
//    		println((knowledges.head :: knowledges.flatMap(_.getChildren) :: Nil).size)
//    		
//    		db.shutdown()
//    		
//    		OrientDbServer.stop        
//    }
    
//	def main(args: Array[String]): Unit = {
////        implicit val db:Graph = GraphDb.get
//        try{
//            OrientDbServer.start
//            
//            implicit val db = new OrientGraph("plocal:/Users/israelfreitas/Documents/workspace/brain/databases/brain_dev")
//            
//            println(Knowledge.findAll.size)
//            
//            db.shutdown()
//            
//            OrientDbServer.stop
//        }
//        finally{
////            db.shutdown()
//        }
//    }

}