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
import com.ansvia.graph.annotation.Persistent
import net.liftweb.json._
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import brain.db.GraphDb
import com.tinkerpop.blueprints.TransactionalGraph
import aimltoxml.aiml.TemplateElement
import aimltoxml.aiml.Text
import aimltoxml.aiml.Category
import aimltoxml.aiml.Srai
import aimltoxml.aiml.Random
import aimltoxml.aiml.Get
import aimltoxml.aiml.AimlSet

case class Teaching(whenTheUserSays:String, say:String) extends DbObject{
    require(!whenTheUserSays.isEmpty, "Field 'when the user says', can not be empty.")
    require(!say.isEmpty, "Field 'say', can not be empty.")
    
    var id:Option[String] = None
	var topicId:Option[String] = None
	
	@Persistent var respondingTo:String = null
	@Persistent var memorize:String = null
    
    def toAiml:Set[Category] = new TeachingToCategoryAdapter(this).toCategory
    
    def save()(implicit db:TransactionalGraph) = transact{
        if(this.respondingTo == null || this.respondingTo.trim().equals("")) this.respondingTo = null
        if(this.memorize == null || this.memorize.trim().equals("")) this.memorize = null
        
        val that = super.save()
        db.getVertex(topicId.get) --> "include" --> that
        that
    }
    
    def destroy() (implicit db:TransactionalGraph) = transact { 
        db removeVertex getVertex 
    }

}

object Teaching extends PersistentName {
    private implicit val formats = net.liftweb.json.DefaultFormats

    implicit def toJson(teaching: Teaching): JValue = JObject(
        JField("id", JString(teaching.id.get.replace("#", ""))) ::
        JField("whenTheUserSays", JString(teaching.whenTheUserSays)) 		::
        JField("respondingTo", JString(teaching.respondingTo)) 			::
        JField("memorize", JString(teaching.memorize)) 				::
        JField("say", JString(teaching.say)) 					::
        JField("topicId", JString(teaching.topicId.get.replace("#", ""))) :: 
        Nil
    )
    
    implicit def topicSetToJValue(topics: Set[Teaching]): JValue = JArray(topics.map(toJson).toList)
    
    def findAll()(implicit db:Graph):Set[Knowledge] = query().vertices().toSet[Vertex].map(v=>Knowledge(v))
    
    def findById(id:String)(implicit db:Graph):Teaching = Teaching(db.getVertex(id))
    
    def findByTopic(topic:Topic)(implicit db:Graph):Set[Teaching] = topic.getVertex.pipe.out("include").iterator.toSet[Vertex].map(v=>Teaching(v))
    
    def apply(in: JValue):Box[Teaching] = Helpers.tryo{
        try {
	        val id = (in \ "id") match {
        	    case id: JString => Some(id.values)
        	    case _ =>  None
        	}
	        val topicId = (in \ "topicId") match {
        	    case topicId: JString => Some(topicId.values)
        	    case _ => None
        	}
	        val whenTheUserSays = (in \ "whenTheUserSays") match {
		        case whenTheUserSays: JString => whenTheUserSays.values
		        case _ =>  ""
	        }
	        val respondingTo = (in \ "respondingTo") match {
		        case respondingTo: JString => Some(respondingTo.values)
		        case _ =>  None
	        }
	        val memorize = (in \ "memorize") match {
		        case memorize: JString => Some(memorize.values)
		        case _ =>  None
	        }
	        val say = (in \ "say") match {
		        case say: JString => say.values
		        case _ =>  ""
	        }
	        
	        val teaching = Teaching(whenTheUserSays, say)
	        teaching.id = id
	        teaching.topicId = topicId
	        respondingTo map { teaching.respondingTo = _}
	        memorize map { teaching.memorize = _}
	        teaching
		}
        catch{
            case t:Throwable => t.printStackTrace(); throw t
        }
    }
    def unapply(in:JValue):Option[Teaching] = apply(in)
    
    def unapply(in:Any):Option[(Option[String], Option[String], Option[String], Option[String], String, String)] = {
        in match {
            case teaching : Teaching => {
               val respondingTo = if(teaching.respondingTo == null) None else Some(teaching.respondingTo)
               val memorize = if(teaching.memorize == null) None else Some(teaching.memorize)
               Some((teaching.id, teaching.topicId, respondingTo, memorize, teaching.whenTheUserSays, teaching.say))
            }
            case id : String => {
            	implicit val db = GraphDb.get
				try{
					val teaching = Teaching.findById(id) 
	        		val respondingTo = if(teaching.respondingTo == null) None else Some(teaching.respondingTo)
	        		val memorize = if(teaching.memorize == null) None else Some(teaching.memorize)
	        		Some((teaching.id, teaching.topicId, respondingTo, memorize, teaching.whenTheUserSays, teaching.say))
				}
	        	catch{
	        	    case t: Throwable => None
	        	}
	        	finally{
	        		db.shutdown()
	        	}
            }
            case _ => None
        }
    }
    
    def apply(vertex:Vertex)(implicit db:Graph):Teaching = {
        val teaching = vertex.toCC[Teaching].get
        teaching.id = Some(vertex.getId.toString)
        teaching.topicId = Some(vertex.pipe.in("include").iterator.next().getId().toString())
        teaching
    }
}

class TeachingToCategoryAdapter(teaching: Teaching) {
	
    require(teaching != null)
    
	/* Para processar o say:
	 *  var frase = "${var1} Ola, ${var2}, ${var3}"
	 *  val regex = """\$\{([a-z0-9]*)\}""".r
	 *  val matches = regex.findAllIn(frase)
	 *  val split   = frase.split(regex.toString)
	 *  val resultado = new scala.collection.mutable.ListBuffer[String]
	 *  split.foreach{f=> resultado += s"${f}${matches.next}" }
	 *  resultado.mkString("")
     */
    
    val respondingTo  : String 	    = if(teaching.respondingTo != null && !teaching.respondingTo.trim().isEmpty()) teaching.respondingTo else "*"
    val whatWasSaid   : Set[String] = nonEmptyLinesToSet(teaching.whenTheUserSays)
    //val whatToMemorize: Set[String] = nonEmptyLinesToSet(teaching.memorize)
    val whatToSay     : Set[String] = nonEmptyLinesToSet(teaching.say)
    
    def toCategory: Set[Category] = {
        val defaultPattern = selectDefaultPattern(whatWasSaid)
        //whatWasSaid.map(createCategory(_, defaultPattern, respondingTo, whatToMemorize, whatToSay))
        whatWasSaid.map(createCategory(_, defaultPattern, whatToSay, respondingTo))
    }
    
    private def nonEmptyLinesToSet(aText:String):Set[String] = aText.split("\n").map(_.trim).toSet[String].filter(!_.trim.isEmpty)
        
    def extractAimlSets(memorize:String):Set[AimlSet] = {
        val keyValues = nonEmptyLinesToSet(memorize)
        if(keyValues.isEmpty){ return Set.empty[AimlSet] }
        keyValues.map{ keyValue =>
            var kv = keyValue.split("=")
            new AimlSet(kv(0).trim,Text(kv(1).trim))
        }
    }

    def selectDefaultPattern(setOfWhatWasSaid: Set[String]) = {
        var defaultPattern         = ""
        var lowerPatternComplexity = 100.0
        var patternComplexity      = 100.0

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

    def createCategory(whatWasSaid: String, defaultPattern: String, whatToSay: Set[String], respondingTo: String):Category = {
        if (whatWasSaid == defaultPattern) Category(whatWasSaid, createTemplateElements(whatToSay), respondingTo)
        else new Category(whatWasSaid, Set(Srai(defaultPattern)), respondingTo)
    }

    def createTemplateElements(say: Set[String]): Set[TemplateElement] = Set(new Random(say.map({Text(_)})))

}

object KeyValueValidator {
    private val notValidCharactersForKeyNameRegex  = """[^a-zA-Z_0-9\-\_]""".r
    private val validCharactersForInitKeyNameRegex = """^[a-zA-Z\_]""".r
    
    def validateKeyValueString(keyValue:String):Unit={
        val split = keyValue.split("=")
        if(split.size == 1) throw new NoAttributionSignException("No equal sign ('=') found in attribution.")
        if(split.size > 2)  throw new MoreThanOneAttributionSignException("The equal sign ('=') must be used only once per line.")
        
        val key   = split(0).trim
        val value = split(1).trim
        
        if(key.isEmpty()) throw new NoVariableNameException("A variable name is required by left hand side of '='. Example: age=30.")
        validateKeyName(key)
    }
    
    def validateKeyName(key:String):Unit={
        if(validCharactersForInitKeyNameRegex.findAllIn(key).isEmpty) throw new InvalidVariableNameException(s"The variable name must start with a letter or an underscore ('_'). Invalid character in: $key")
        if(! notValidCharactersForKeyNameRegex.findAllIn(key).isEmpty) throw new InvalidVariableNameException(s"The variable name must have only letters (without signs or spaces), numbers and symbols '-' and '_'. Invalid character in: $key")
    }
}

class NoAttributionSignException(cause:String) extends Exception(cause)
class MoreThanOneAttributionSignException(cause:String) extends Exception(cause)
class NoVariableNameException(cause:String) extends Exception(cause)
class InvalidVariableNameException(cause:String) extends Exception(cause)
class NoValueContentException(cause:String) extends Exception(cause)
