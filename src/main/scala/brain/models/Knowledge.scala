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
package brain.models

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph

import aimltoxml.aiml.Aiml

case class Knowledge(val name: String, val topics: Set[Topic], val children: Set[Knowledge]) {
	require(name != null && !name.isEmpty(), "Name is required.")
	
	def toAiml:Aiml = new KnowledgeToAimlAdapter(this).toAiml
	
	def canEqual(other: Any) = {
		other.isInstanceOf[brain.models.Knowledge]
	}
	
	override def equals(other: Any) = {
		other match {
		case that: brain.models.Knowledge => that.canEqual(Knowledge.this) && name == that.name && topics == that.topics
		case _                            => false
		}
	}
	
	override def hashCode() = {
		val prime = 41
				prime * (prime + name.hashCode) + topics.hashCode
	}
	
	// TODO Do in a Lift way.
	def toJsonString:String = s"{id: 1, name: $name, data:{topics: [${topics.map(_.name).mkString(",")}]}, children:[${children.map(c =>c.toJsonString).mkString(",")}]}"
}


class KnowledgeToAimlAdapter(knowledge: Knowledge) {
    def toAiml = Aiml(knowledge.name, knowledge.topics.map(_.toAiml))
}
