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

import aimltoxml.aiml.TemplateElement
import com.ansvia.graph.BlueprintsWrapper.DbObject
import aimltoxml.aiml.Text
import aimltoxml.aiml.Category
import aimltoxml.aiml.Srai
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import brain.db.GraphDb

case class Teaching(val id:String, val informationId:String, val whenTheUserSays:String, val respondingTo:String, val memorize:String, val say:String){
    require(!whenTheUserSays.isEmpty, "Field 'when the user says', can not be empty.")
    require(!say.isEmpty, "Field 'say', can not be empty.")
    
    def toAiml = new TeachingToCategoryAdapter(this).toCategory
    
    def toJson:String = raw"{id:'$id', informationId:'$informationId', whenTheUserSays:'$whenTheUserSays', respondingTo:'$respondingTo', memorize:'$memorize',say:'$say'}" 
}

object Teaching{
	def findById(id:String)(implicit db:Graph):Set[Teaching]  = Set.empty[Teaching]
	def findByInformationId(informationId:String)(implicit db:Graph):Set[Teaching]  = Set.empty[Teaching]
    def create(teaching:Teaching)(implicit db:OrientGraph):Option[Teaching] = {
        val vertex = GraphDb.transaction[Vertex]({
        	val teachingVertex    = db.addVertex("class:Teaching", "whenTheUserSays", teaching.whenTheUserSays, "respondingTo", teaching.respondingTo, "memorize", teaching.memorize, "say", teaching.say)
            val informationVertex = db.getVertex(teaching.informationId)
            db.addEdge("class:Include", informationVertex, teachingVertex, "include")
            teachingVertex
        })
        vertex.map(v=>Teaching(v))
	}
    def update(teaching:Teaching)(implicit db:OrientGraph):Option[Teaching] = {
        val result = GraphDb.transaction[Vertex] {
            var oldTeaching = db.getVertex(teaching.id)
            oldTeaching.setProperty("whenTheUserSays", teaching.whenTheUserSays)
            oldTeaching.setProperty("respondingTo", teaching.respondingTo)
            oldTeaching.setProperty("memorize", teaching.memorize)
            oldTeaching.setProperty("say", teaching.say)
            oldTeaching
        }
        result.map(v=>Teaching(v))
    }
    def delete(teaching:Teaching)(implicit db:OrientGraph):Teaching = teaching
    
    def apply(vertex:Vertex):Teaching = Teaching(vertex.getId().toString(), "", vertex.getProperty("whenTheUserSays"), vertex.getProperty("respondingTo"), vertex.getProperty("memorize"), vertex.getProperty("say"))
}

class TeachingToCategoryAdapter(teaching: Teaching) {

    val whatWasSaid: Set[String] = teaching.whenTheUserSays.split("\r\n").toSet[String].filter(!_.isEmpty)
    val whatToSay: Set[String] = teaching.say.split("\r\n").toSet[String].filter(!_.isEmpty)
    val respondingTo = teaching.respondingTo

    def selectDefaultPattern(setOfWhatWasSaid: Set[String]) = {
        var defaultPattern = ""
        var lowerPatternComplexity = 100.0
        var patternComplexity = 100.0

        setOfWhatWasSaid.foreach { whatWasSaid =>
            patternComplexity = calculateThePatternComplexity(whatWasSaid)
            if (patternComplexity < lowerPatternComplexity) {
                lowerPatternComplexity = patternComplexity
                defaultPattern = whatWasSaid
            }
        }
        defaultPattern
    }

    // it should be a selectDefaultPattern's local function, but is not for tests purposes.
    def calculateThePatternComplexity(pattern: String): Double = {
        def countStarsIn(p: String) = countSpecialChar("*", p)
        def countUnderscoreIn(p: String) = countSpecialChar("_", p)

        val amountOfChar = pattern.length
        val amountOfStar = countStarsIn(pattern)
        val amountOfUnderscore = countUnderscoreIn(pattern)

        amountOfChar * 0.001 + amountOfStar * 1 + amountOfUnderscore * 1
    }

    // it should be a calculateThePatternComplexity's local function, but is not for tests purposes.
    def countSpecialChar(c: String, p: String) = { p.split("\\" + c + "+", -1).size - 1 }

    def createCategory(whatWasSaid: String, defaultPattern: String, respondingTo: String, say: Set[String]) = {
        if (whatWasSaid == defaultPattern) Category(whatWasSaid, createTemplateElements(say))
        else Category(whatWasSaid, Set(Srai(defaultPattern)))
    }

    def createTemplateElements(say: Set[String]): Set[TemplateElement] = say.map { Text(_) }

    def toCategory: Set[Category] = {
        val defaultPattern = selectDefaultPattern(whatWasSaid)
        whatWasSaid.map(createCategory(_, defaultPattern, respondingTo, whatToSay))
    }
}
