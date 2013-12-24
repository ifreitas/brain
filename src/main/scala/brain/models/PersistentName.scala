package brain.models

import com.tinkerpop.blueprints.GraphQuery
import com.tinkerpop.blueprints.Graph

class PersistentName {
	final def persistentName={
	    val r = s"${this.getClass().getName().replaceAll("\\$", "")}"
	    println(r)
	    r
	}
	
	def query()(implicit db:Graph):GraphQuery = {
	    db.query().has("_class_", persistentName)
	}
}