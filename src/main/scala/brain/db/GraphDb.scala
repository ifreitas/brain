/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Israel Freitas -- ( gmail => israel.araujo.freitas)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
    //
    // Observacoes:
    // 1. O grafo do OrientDB é automaticamente setado 
    //    no ThreadLocal quando é instanciado.
    // 2. O grafo retornado aqui pode ser um singleton.
    // 3. O grafo do Orientdb nao e pooled por isso es-
    //	ta  sendo  construido  a  partir  do  Ograph-
    //	Database. (Ver: https://groups.google.com/forum/#!topic/orient-database/ozzG-ELEtkQ)
    //    
    def get: OrientGraph = {
        var instance = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined()
        if (instance != null && !instance.isClosed())
            // minha original
            //new OrientGraph(new OGraphDatabase(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))

            // https://groups.google.com/forum/#!topic/orient-database/INlnRO2Fv_Y
            // TODO: Testar usando o "ODatabaseDocumentTxPooled"
            new OrientGraph(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
        else {
            new OrientGraph(OGraphDatabasePool.global().acquire(Config.getGraphDbUri, Config.getGraphDbUser, Config.getGraphDbPassword))
        }
    }
	def getNoTx: OrientGraphNoTx = {
		if (ODatabaseRecordThreadLocal.INSTANCE.isDefined())
			new OrientGraphNoTx(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
		else {
			new OrientGraphNoTx(OGraphDatabasePool.global().acquire(Config.getGraphDbUri, Config.getGraphDbUser, Config.getGraphDbPassword))
		}
    }
	
    def transaction[T](func: => T)(implicit db:OrientGraph):Option[T]={
        try {
        	val result = func
        	db.commit
        	return Some(result)
        }
        catch{
            case t : Throwable => db.rollback; throw t
        }
        finally { if( !db.isClosed ) db.shutdown }
    }

}