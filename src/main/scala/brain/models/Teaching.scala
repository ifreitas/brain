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

case class Teaching(whenUserSays: Option[Set[String]], respondingTo: Option[String], say: Option[Set[String]]) extends DbObject {
    def toAiml: Set[Category] = { new TeachingToCategoryAdapter(this).toCategory }

    def isValid: Boolean = { false }
}

class TeachingToCategoryAdapter(teaching: Teaching) {
    require(teaching != null, "The teaching is required to be converted to a Category.")
    require(teaching.isValid, "The teaching must be valid in order to be converted to a Category.")

    val whatWasSaid: Set[String] = teaching.whenUserSays.get.filter(whenUserSays => whenUserSays.trim().size > 0)
    val whatToSay: Set[String] = teaching.say.get.filter(say => say.trim().size > 0)
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
        def countStarsIn(p: String) = { countSpecialChar("*", p) }
        def countUnderscoreIn(p: String) = { countSpecialChar("_", p) }

        val amountOfChar = pattern.length()
        val amountOfStar = countStarsIn(pattern)
        val amountOfUnderscore = countUnderscoreIn(pattern)

        amountOfChar * 0.001 + amountOfStar * 1 + amountOfUnderscore * 1
    }

    // it should be a calculateThePatternComplexity's local function, but is not for tests purposes.
    def countSpecialChar(c: String, p: String) = { p.split("\\" + c + "+", -1).size - 1 }

    def createCategory(whatWasSaid: String, defaultPattern: String, respondingTo: String, say: Set[String]) = {
        if (whatWasSaid == defaultPattern) Category(whatWasSaid, createTemplateElements(say))
        else Category(whatWasSaid, Srai(defaultPattern))
    }

    def createTemplateElements(say: Set[String]): Set[TemplateElement] = {
        say.map { Text(_) }
    }

    def toCategory: Set[Category] = {
        val defaultPattern = selectDefaultPattern(teaching.whenUserSays.get)
        whatWasSaid.map(wasSaid => createCategory(wasSaid, defaultPattern, "", whatToSay))
    }
}
