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
package brain.db

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import brain.config.Config
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx

object GraphDb {
    def get: OrientGraph = {
        var instance = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined()
        if (instance != null && !instance.isClosed())
            new OrientGraph(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
        else {
            new OrientGraph(Config.getGraphDbUri)
        }
    }
	def getNoTx: OrientGraphNoTx = {
	    println("(ntx) trying to connect to: " + Config.getGraphDbUri)
		if (ODatabaseRecordThreadLocal.INSTANCE.isDefined())
			new OrientGraphNoTx(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
		else {
			new OrientGraphNoTx(Config.getGraphDbUri)
		}
    }
	
}