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

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterEach
import aimltoxml.aiml.Category
import aimltoxml.aiml.Text
import aimltoxml.aiml.Srai
import aimltoxml.aiml.TemplateElement
import aimltoxml.aiml.Random
import org.scalatest.FlatSpec

class TeachingToCategoryAdapterTest extends FunSpec with Matchers with BeforeAndAfter {

    var adapter: TeachingToCategoryAdapter = null
    var teaching: Teaching = null
    var validTeaching: Teaching = null

    before {
        validTeaching = mock(classOf[Teaching])
        when(validTeaching.whenTheUserSays).thenReturn("""	hi
        												  	hello
                											hello there""")
        when(validTeaching.say).thenReturn("hi")
    }

    describe("#TeachingToCategoryAdapter") {
        def setup = {
            teaching = mock(classOf[Teaching])
        }

        it("requires a valid Teaching") {
            setup
            new TeachingToCategoryAdapter(validTeaching)
        }
        it("when the teaching is null throws an exception") {
            setup
            intercept[IllegalArgumentException](new TeachingToCategoryAdapter(null))
        }
        it("when present, requires a valid 'respondingTo'")(pending)
        it("when present, requires a valid 'think'")(pending)
    }
    describe("#selectDefaultPattern") {
        def setup = {
            adapter = new TeachingToCategoryAdapter(validTeaching)
        }
        it("returns the more simple pattern based on the its complexity") {
            setup
            adapter.selectDefaultPattern(Set("hi")) should be("hi")
            adapter.selectDefaultPattern(Set("hello there", "hey")) should be("hey")

            // with star
            adapter.selectDefaultPattern(Set("hello there", "hey *")) should be("hello there")
            adapter.selectDefaultPattern(Set("hello there *", "hey *")) should be("hey *")
            adapter.selectDefaultPattern(Set("hey * *", "hey *")) should be("hey *")

            // with underscore
            adapter.selectDefaultPattern(Set("hello there", "hey _")) should be("hello there")
            adapter.selectDefaultPattern(Set("hello there _", "hey _")) should be("hey _")
            adapter.selectDefaultPattern(Set("hey _ _", "hey _")) should be("hey _")
        }
    }

    describe("#calculateThePatternComplexity") {
        def setup = {
            adapter = new TeachingToCategoryAdapter(validTeaching)
        }
        it("considers the amount of chars") {
            setup
            adapter.calculateThePatternComplexity("hi") should be(0.002)
        }
        it("considers the amount of stars") {
            setup
            adapter.calculateThePatternComplexity("*") should be(1.001)
            adapter.calculateThePatternComplexity("* *") should be(2.003)
        }
        it("considers the amount of underscores") {
            setup
            adapter.calculateThePatternComplexity("_") should be(1.001)
            adapter.calculateThePatternComplexity("_ _") should be(2.003)
        }
    }

    describe("#countSpecialChar") {
        def setup = {
            adapter = new TeachingToCategoryAdapter(validTeaching)
        }
        it("count united special char as only one star") {
            setup
            adapter.countSpecialChar("*", "one *** sequence ") should be(1)
            adapter.countSpecialChar("*", "*** first") should be(1)
            adapter.countSpecialChar("*", "last **") should be(1)
            adapter.countSpecialChar("*", "one *** two ****** sequences ") should be(2)
            adapter.countSpecialChar("*", "one *** two ****** three ** sequences ") should be(3)
        }
        it("returns 0 when no special char is found") {
            setup
            adapter.countSpecialChar("*", "pattern without any star") should be(0)
        }
        it("returns the number of occurrences of the special character") {
            setup
            adapter.countSpecialChar("*", "pattern with only one *") should be(1)
            adapter.countSpecialChar("*", "* first") should be(1)
            adapter.countSpecialChar("*", "last *") should be(1)
            adapter.countSpecialChar("*", "mid*dle") should be(1)
            adapter.countSpecialChar("*", "pattern with only one * ") should be(1)
            adapter.countSpecialChar("*", "pattern with only one * and two *") should be(2)
            adapter.countSpecialChar("*", "pattern with only one * and two * ") should be(2)
            adapter.countSpecialChar("*", "pattern with only one * and two * and three *") should be(3)
        }
    }

    describe("#createCategory") {
        def setup = {
            adapter = new TeachingToCategoryAdapter(validTeaching)
        }
        it("when the pattern is equals to the default pattern then returns a category with the fully filled template") {
            val expectedCategory = Category("hi", Random("hello there"))
            adapter.createCategory("hi", "hi", Set("hello there"), "*") should be(expectedCategory)
        }
        it("when the pattern is not equals to the default pattern then returns a category which template contains only a srai pointing to the default pattern") {
            val expectedCategory = Category("hello", Srai("hi"))
            adapter.createCategory("hello", "hi", Set("hello there"), "*") should be(expectedCategory)
        }
    }

    describe("#toCategory") {
        def setup = {
            adapter = new TeachingToCategoryAdapter(validTeaching)
        }
        it("returns a set with a category for each sentence said by the user") {
            val categories = adapter.toCategory
            categories.size should be(3)
            categories.map(_.pattern) should be(Set(Text("hi"), Text("hello"), Text("hello there")))
            categories.toList.map(_.templateElements.head) should be(List(Random("hi"), Srai("hi"), Srai("hi")))
        }
    }
    
    
    describe("#validateAttributions"){
        def setup = {
        }
        it("throws an exception if there is no '='"){
            intercept[NoAttributionSignException](KeyValueValidator.validateKeyValueString(""))
            intercept[NoAttributionSignException](KeyValueValidator.validateKeyValueString("someKey"))
        }
        it("throws an exception if there is more than one '='"){ 
            intercept[MoreThanOneAttributionSignException](KeyValueValidator.validateKeyValueString("k1=v1 k2=v2"))
            intercept[MoreThanOneAttributionSignException](KeyValueValidator.validateKeyValueString("k1==v1"))
        }
        it("throws an exception if there is no Key"){
            intercept[NoVariableNameException](KeyValueValidator.validateKeyValueString(" =value"))
        }
        it("validates the Key name"){
            //verify(mock, times(1)).validateKeyName("???")
            pending
        }
        it("throws an exception if empty 'get' (${}) is present in Value"){ pending }
        it("throws an exception if Value part contains space ' ' between the variable name ('age=${some value})"){ pending } 
        it("throws an exception if unclosed 'get' (${) is present in right hand side"){ pending }
    }
    
    describe("validateKeyName"){
    	it("throws an exception if there is no Var"){
            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName(""))
        }
//        it("throws an exception if Key does not starts with a letter"){
//            //certo
//            KeyValueValidator.validateKeyName("_userName=value")
//            KeyValueValidator.validateKeyName("userName=value")
//            KeyValueValidator.validateKeyName("UserName=value")
//            
//            //errado
//            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("$=value"))
//            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("@=value"))
//            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("1a=value"))
//            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName(".a=value"))
//            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName(" á=value"))
//        }
        it("throws an exception if Key does not starts with a letter"){
        	//certo
        	KeyValueValidator.validateKeyName("_userName")
        	KeyValueValidator.validateKeyName("userName")
        	KeyValueValidator.validateKeyName("UserName")
        	
        	//errado
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("$name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("@name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("1name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName(".name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("áname"))
        }
        it("throws an exception if Key part contains space ' ' between the variable name"){
            intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user name"))
        } 
//        it("throws an exception if Key part contains something differ from [a-zA-Z_0-9\\_\\-]"){
//        	//correto:
//        	KeyValueValidator.validateKeyName("username=value")
//        	KeyValueValidator.validateKeyName("userName=value")
//        	KeyValueValidator.validateKeyName("UserName=value")
//        	KeyValueValidator.validateKeyName("user_name=value")
//        	KeyValueValidator.validateKeyName("user-name=value")
//        	KeyValueValidator.validateKeyName("_userName=value")
//        	
//        	//errado:
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user.name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user@name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("#name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("%name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("&name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("(name=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("tést=value"))
//        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("n@me=value"))
//        }
        it("throws an exception if Key part contains something differ from [a-zA-Z_0-9\\_\\-]"){
            //correto:
        	KeyValueValidator.validateKeyName("username")
        	KeyValueValidator.validateKeyName("userName")
        	KeyValueValidator.validateKeyName("UserName")
        	KeyValueValidator.validateKeyName("user_name")
        	KeyValueValidator.validateKeyName("user-name")
        	KeyValueValidator.validateKeyName("_userName")
            
            //errado:
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user.name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("user@name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("n#me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("n%me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("nam&"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("na(me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("tést"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKeyName("n@me"))
        }
    }

}
