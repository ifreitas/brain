package brain.models

import com.tinkerpop.blueprints.GraphQuery
import com.tinkerpop.blueprints.Graph

class PersistentName {
	final def persistentName = s"${this.getClass().getName().replaceAll("\\$", "")}"
	
	def query()(implicit db:Graph):GraphQuery = db.query().has("_class_", persistentName)
}