package brain.db

import com.tinkerpop.blueprints.Graph
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import com.orientechnologies.orient.core.db.graph.OGraphDatabase
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTxPooled
import brain.config.Config
import com.tinkerpop.blueprints.TransactionalGraph
import java.io.File

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
    def get: Graph = {
        if (ODatabaseRecordThreadLocal.INSTANCE.isDefined())
            // minha original
            //new OrientGraph(new OGraphDatabase(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))

            // https://groups.google.com/forum/#!topic/orient-database/INlnRO2Fv_Y
            // TODO: Testar usando o "ODatabaseDocumentTxPooled"
            new OrientGraph(new ODatabaseDocumentTx(ODatabaseRecordThreadLocal.INSTANCE.get.asInstanceOf[ODatabaseRecordTx]))
        else {
            new OrientGraph(OGraphDatabasePool.global().acquire(Config.getGraphDbUri, Config.getGraphDbUser, Config.getGraphDbPassword))
        }
    }

}