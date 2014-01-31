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
import aimltoxml.aiml.AimlSet
import scala.collection.mutable.ListBuffer
import aimltoxml.aiml.Get
import aimltoxml.aiml.Think
import aimltoxml.aiml.RandomElement
import aimltoxml.aiml.Star

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
    
    /**
     * 
     * var frase = "${var1} Ola, ${var2}, ${var3}"
     * val regex = """\$\{([a-z0-9]*)\}""".r
     * val matches = regex.findAllIn(frase)
     * val split   = frase.split(regex.toString)
     * val resultado = new scala.collection.mutable.ListBuffer[String]
     * split.foreach{f=> resultado += s"${f}${matches.next}" }
     * resultado.mkString("")
     * 
     */
	
    require(teaching != null)
    
    val respondingTo  : String 	    = if(teaching.respondingTo != null && !teaching.respondingTo.trim().isEmpty()) teaching.respondingTo else "*"
    val whatWasSaid   : Set[String] = linesToSet(teaching.whenTheUserSays)
    val whatToMemorize: List[String] = linesToList(teaching.memorize)
    val whatToSay     : Set[String] = linesToSet(teaching.say)
    
    val GetSyntaxRegex = """\$\{([a-zA-Z_0-9\-\_\*]*)\}""".r
    val IndexRegex = """(\d+)""".r
    
    def toCategory: Set[Category] = {
        val defaultPattern = selectDefaultPattern(whatWasSaid)
        whatWasSaid.map(createCategory(_, defaultPattern, respondingTo, whatToMemorize, whatToSay))
    }
    
   	def createCategory(whatWasSaid: String, defaultPattern: String, respondingTo: String, whatToMemorize: List[String], whatToSay: Set[String]):Category = {
        if (whatWasSaid == defaultPattern) Category(whatWasSaid, createTemplateElements(whatToMemorize, whatToSay), respondingTo)
        else new Category(whatWasSaid, Set(Srai(defaultPattern)), respondingTo)
    }
   	
    def createTemplateElements(memorize: List[String], say: Set[String]): Set[TemplateElement] = {
        val think:Think = Think(parseMemorize(memorize))
        val listOfWhatToSay:Set[List[RandomElement]] = parseSay(say.toList)
        Set(think, new Random(listOfWhatToSay))
    }
    
    def parseMemorize(whatToMemorize: List[String]):List[AimlSet]  = whatToMemorize.map{parseKeyValue(_)}
    def parseSay(whatToSay: List[String]):Set[List[RandomElement]] = whatToSay.map{parseValue(_)}.toSet.asInstanceOf[Set[List[RandomElement]]]
    
    def parseKeyValue(keyValueString:String):AimlSet = {
        KeyValueValidator.validateKeyValueString(keyValueString)
        val keyValue   = keyValueString.split("=")
        AimlSet(keyValue(0).trim(), parseValue(keyValue(1)))
    }
    
    /**
     * 
     * - "test"          => Text('test')
     * - "${test}"       => Get("test")
     * - "hello ${name}" => Text("hello "), Get("name")
     */
    def parseValue(valueString:String):List[TemplateElement] = {
		val iteratorOfGet = GetSyntaxRegex.findAllIn(valueString).map(g=>parseGet(g))
        val result: ListBuffer[TemplateElement] = new ListBuffer
        val splitedValue = valueString.split(GetSyntaxRegex.toString)
        
        if(splitedValue.isEmpty){
            while(iteratorOfGet.hasNext)result.add(iteratorOfGet.next)
        }
        else{
        	splitedValue.foreach{txt=>
	            result.add(Text(txt));
	            if(iteratorOfGet.hasNext)result.add(iteratorOfGet.next)
	    	}
        }
    	result.toList
    }
    
    /**
     * ${} 		=> exception
     * ${*}		=> Star(1)
     * ${*i}	=> Star(i) // 'i' as integer
     * ${someName}	=> Get(someName)
     */
    def parseGet(getSyntaxString:String):TemplateElement = {
        val get = GetSyntaxRegex.findFirstMatchIn(getSyntaxString)
		var starIndexRegex = """[^\*]+""".r
        
        if(get.isEmpty) throw new InvalidGetSyntaxException(s"No get syntax match in $getSyntaxString")
        
        get.get.group(1) match{
            case star if star.trim.startsWith("*") => {
                try {
        			var index = starIndexRegex.findFirstIn(star).getOrElse("1").toInt
                	if(index < 1) throw new InvalidStarIndexException(s"The star's index must be greater than 0. Please fix '$star'")
        			Star(index)
                }
                catch{
                    case numberFormatException: NumberFormatException => throw new InvalidStarIndexException(s"Only numbers can be used to access star's index. Please fix '$star' ('${"""[^\*]+""".r.findFirstIn(star).get}')")
                }
            }
            case name if(!name.trim.contains(" "))=> Get(name.trim)
            case other => throw new InvalidGetSyntaxException(s"Invalid get syntax excetion in '${other}'")
        }
    }
    
    private def linesToSet(aText:String):Set[String]   = if(aText == null)Set.empty[String]  else aText.split("\n").map(_.trim).filter(!_.isEmpty).toSet
	private def linesToList(aText:String):List[String] = if(aText == null)List.empty[String] else aText.split("\n").map(_.trim).filter(!_.isEmpty).toList
        
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
        
        if(key.isEmpty()) throw new NoVariableNameException("A variable name is required by left hand side of '='. Example: age = ...")
        validateKeyName(key)
    }
    
    def validateKeyName(key:String):Unit={
        if(validCharactersForInitKeyNameRegex.findAllIn(key).isEmpty) throw new InvalidVariableNameException(s"The variable name must start with a letter or an underscore ('_'). Invalid character in: $key")
        if(!notValidCharactersForKeyNameRegex.findAllIn(key).isEmpty) throw new InvalidVariableNameException(s"The variable name must have only letters (without signs or spaces), numbers and symbols '-' and '_'. Invalid character in: $key")
    }
}

class NoAttributionSignException(cause:String) extends Exception(cause)
class MoreThanOneAttributionSignException(cause:String) extends Exception(cause)
class NoVariableNameException(cause:String) extends Exception(cause)
class InvalidVariableNameException(cause:String) extends Exception(cause)
class NoValueContentException(cause:String) extends Exception(cause)
class InvalidStarIndexException(cause:String) extends Exception(cause)
class InvalidGetSyntaxException(cause:String) extends Exception(cause)
