/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Israel Freitas(israel.araujo.freitas@gmail.com)
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
import aimltoxml.aiml.Think
import aimltoxml.aiml.Star
import aimltoxml.aiml.Get

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
            val expectedCategory = new Category("hi", Set(Think(List.empty[TemplateElement]), Random("hello there")))
            adapter.createCategory("hi", "hi", "*", List.empty[String], Set("hello there")) should be(expectedCategory)
        }
        it("when the pattern is not equals to the default pattern then returns a category which template contains only a srai pointing to the default pattern") {
            val expectedCategory = Category("hello", Srai("hi"))
            adapter.createCategory("hello", "hi", "*", List.empty[String], Set("hello there")) should be(expectedCategory)
        }
    }
    
    describe("#createTemplateElements"){ pending }
    describe("#parseMemorize"){ pending }
    describe("#parseSay"){ pending }
    describe("#parseKeyValue"){ pending }
    describe("#parseValue"){ pending }
    
    describe("parseText"){
        def setup = {
    		adapter = new TeachingToCategoryAdapter(validTeaching)
    	}
    	it("return List(Text) if there is no getSyntax"){
    		adapter.parseText("only text") should be (List(Text("only text")))
    	}
        it("return List(Text, Get) if there is getSyntax"){
        	adapter.parseText("text and ${get}") should be (List(Text("text and "), Get("get")))
        }
        it("return List(Text, Star) if there is getSyntax(*)"){
        	adapter.parseText("text and ${*}") should be (List(Text("text and "), Star(1)))
        }
        it("return List(Text, Get, Star) if there is getSyntax(*)"){
        	adapter.parseText("text, ${get} and ${*}") should be (List(Text("text, "), Get("get"), Text(" and "), Star(1)))
        }
        it("return List(Get, Star) if there is getSyntax(*)"){
        	adapter.parseText("${get}${*}") should be (List(Get("get"), Star(1)))
        }
    }
    
    //GetUtil ####
    describe("GetUtil.parse"){
    	it("return Get(test) in '${ test }'"){
    		GetUtil.parse("${   test   }") should be (Get("test"))
    	}
    	it("return Star(1) in '${ *1 }'"){
    		GetUtil.parse("${ *1 }") should be (Star(1))
    	}
    	it("return Star(1) if equals to '${*}'"){
    		GetUtil.parse("${*1}") should be (Star(1))
    	}
    	it("return Star(i) if equals to '${*i}' (i=1)"){
    		GetUtil.parse("${*1}") should be (Star(1))
    	}
    	it("return Star(i) if equals to '${*i}' (i=20)"){
    		GetUtil.parse("${*20}") should be (Star(20))
    	}
    	it("throws an exception if the get is empty ('${}')"){
    		intercept[InvalidGetSyntaxException](GetUtil.parse("${}"))
    	}
    }
    describe("GetUtil.validate"){
    	it("throws an exception if i is not a Number in ${*i}"){
    		intercept[InvalidStarIndexException](GetUtil.validate("${*aaa}"))
    	}
    	it("throws an exception if i < 1"){
    		intercept[InvalidStarIndexException](GetUtil.validate("${*0}"))
    	}
    	it("throws an exception if the get is empty ('${}')"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("${}"))
    	}
    	it("throws an exception if the get is empty ('${   }')"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("${   }"))
    	}
    	it("throws an exception if getSyntax has a name with empty space (${some name})"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("${some name}"))
    	}
    	it("throws an exception if getSyntax has a name with empty space (${* 1})"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("${* 1}"))
    	}
    	it("throws an exception if getSyntax does not match"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("*1"))
    	}
    	it("throws an exception if getSyntax does not match (2)"){
    		intercept[InvalidGetSyntaxException](GetUtil.validate("some text"))
    	}
    	it("throws an exception get's Name starts with an invalid char"){
	        intercept[InvalidVariableNameException](GetUtil.validate("${-name}"))
	        intercept[InvalidVariableNameException](GetUtil.validate("${$name}"))
	    	intercept[InvalidVariableNameException](GetUtil.validate("${@name}"))
	    	intercept[InvalidVariableNameException](GetUtil.validate("${1name}"))
	    	intercept[InvalidVariableNameException](GetUtil.validate("${.name}"))
	    	intercept[InvalidVariableNameException](GetUtil.validate("${áname}"))
    	}
    	it("throws an exception get's Name contains any invalid char"){
        	intercept[InvalidVariableNameException](GetUtil.validate("${user.name}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${user@name}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${n#me}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${n%me}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${nam&}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${na(e}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${tést}"))
        	intercept[InvalidVariableNameException](GetUtil.validate("${n@me}"))
    	}
    	it("pass ok if syntax ok"){
    		GetUtil.validate("${*1}")
    		GetUtil.validate("${ *1 }")
    		GetUtil.validate("${name}")
    		GetUtil.validate("${ name }")
    	}
    }
    describe("GetUtil.findInvalidCharacterForInitializeGetName"){
        it("return all char that differ from [a-zA-Z_0-9\\_\\*]"){
        	GetUtil.findInvalidCharacterForInitializeGetName("name") should be (None)
        	GetUtil.findInvalidCharacterForInitializeGetName("*name")should be (None)
        	
	        GetUtil.findInvalidCharacterForInitializeGetName("-name")should be (Some("-"))
	        GetUtil.findInvalidCharacterForInitializeGetName("$name")should be (Some("$"))
	    	GetUtil.findInvalidCharacterForInitializeGetName("@name")should be (Some("@"))
	    	GetUtil.findInvalidCharacterForInitializeGetName("1name")should be (Some("1"))
	    	GetUtil.findInvalidCharacterForInitializeGetName(".name")should be (Some("."))
	    	GetUtil.findInvalidCharacterForInitializeGetName("áname")should be (Some("á"))
        }
    }
   describe("GetUtil.findInvalidCharacterForGetName"){
    	it("return all char that differ from [a-zA-Z_0-9\\_\\-\\*]"){
        	GetUtil.findInvalidCharacterForGetName("user name") should be (Some(" "))
        	GetUtil.findInvalidCharacterForGetName("user.name") should be (Some("."))
        	GetUtil.findInvalidCharacterForGetName("user@name") should be (Some("@"))
        	GetUtil.findInvalidCharacterForGetName("n#me") should be (Some("#"))
        	GetUtil.findInvalidCharacterForGetName("n%me") should be (Some("%"))
        	GetUtil.findInvalidCharacterForGetName("nam&") should be (Some("&"))
        	GetUtil.findInvalidCharacterForGetName("na(me") should be (Some("("))
        	GetUtil.findInvalidCharacterForGetName("tést") should be (Some("é"))
        	GetUtil.findInvalidCharacterForGetName("n@me") should be (Some("@"))
    	}
    }
   describe("GetUtil.find"){
       it("return Some(${}) in 'this is an empty get ${}'"){
           GetUtil.findIn("this is an empty get ${}") should be (Some("${}"))
       }
       it("return Some(${   }) in 'this is an empty get ${   }'"){
    	   GetUtil.findIn("this is an empty get ${   }") should be (Some("${   }"))
       }
       it("return Some(${a}) in 'this is an non-empty get ${a}'"){
    	   GetUtil.findIn("this is an empty get ${a}") should be (Some("${a}"))
       }
       it("return Some(${a}) in 'this is an non-empty get ${ a }'"){
    	   GetUtil.findIn("this is an empty get ${a}") should be (Some("${a}"))
       }
       it("return Some(${#a}) in 'this is an non-empty get ${ #a }'"){
    	   GetUtil.findIn("this is an empty get ${#a}") should be (Some("${#a}"))
       }
       it("return Some(${${a}) in 'this is an non-empty get ${ ${a} }'"){
    	   GetUtil.findIn("this is an empty get ${${a}}") should be (Some("${${a}"))
       }
       it("return None in 'this is an text with get syntax'"){
    	   GetUtil.findIn("this is an text with get syntax") should be (None)
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
            categories.toList(0).templateElements should be( Set(Think(List()), new Random(Set(List(Text("hi"))))))
            categories.toList(1).templateElements should be( Set(Srai("hi")))
            categories.toList(2).templateElements should be( Set(Srai("hi")) )
        }
    }
    
    
    describe("#validateKeyValue"){
        def setup = {
        }
        it("throws an exception if there is no '='"){
            // should not raise an error!
            KeyValueValidator.validateKeyValue("test=")
            intercept[NoAttributionSignException](KeyValueValidator.validateKeyValue(""))
            intercept[NoAttributionSignException](KeyValueValidator.validateKeyValue("someKey"))
        }
        it("throws an exception if there is more than one '='"){ 
            intercept[MoreThanOneAttributionSignException](KeyValueValidator.validateKeyValue("k1=v1 k2=v2"))
            intercept[MoreThanOneAttributionSignException](KeyValueValidator.validateKeyValue("k1==v1"))
        }
        it("throws an exception if there is no Key"){
            intercept[NoVariableNameException](KeyValueValidator.validateKeyValue(" =value"))
        }
        it("validates the Key name"){
            //verify(mock, times(1)).validateKeyName("???")
            pending
        }
        it("throws an exception if empty 'get' (${}) is present in Value"){ pending }
        it("throws an exception if Value part contains space ' ' ('age=${some value})"){ pending } 
        it("throws an exception if unclosed 'get' (${) is present"){ pending }
    }
    
    //KeyValueUtil
    describe("findKey"){
    	it("return Some('k') (without space) in 'k=value'"){
    		val key = KeyValueUtil.findKey("k=value")
			if(key.isEmpty) throw new Exception("key not found!")
    		key.get should be ("k")
    	}
        it("return Some('key') (without space) in ' key =value'"){
            val key = KeyValueUtil.findKey(" key = value")
            if(key.isEmpty) throw new Exception("key not found!")
            key.get should be ("key")
        }
        it("return Some('key') (without space) in '   key =value'"){
        	val key = KeyValueUtil.findKey(" key = value")
			if(key.isEmpty) throw new Exception("key not found!")
        	key.get should be ("key")
        }
        it("return Some('key') (without space) in '   key   =value'"){
        	val key = KeyValueUtil.findKey(" key = value")
			if(key.isEmpty) throw new Exception("key not found!")
        	key.get should be ("key")
        }
        it("return Some('a key') (without space) in ' a key =value'"){
            // \s is an invalid character, but it does not matter in this moment.
        	val key = KeyValueUtil.findKey("@key = value")
        	if(key.isEmpty) throw new Exception("key not found!")
        	key.get should be ("@key")
        }
        it("return Some('@key') (without space) in ' key =value'"){
        	// @ is an invalid start character name, but it does not matter in this moment.
        	val key = KeyValueUtil.findKey("@key = value")
			if(key.isEmpty) throw new Exception("key not found!")
        	key.get should be ("@key")
        }
        
        it("return None in '        =value'"){
            KeyValueUtil.findKey("        = value") should be (None)
        }
        it("return None in '=value'"){
        	KeyValueUtil.findKey("= value") should be (None)
        }
    }
    
    describe("#findInvalidCharacterForInitializeKeyName"){
        it("return all char that differ from [a-zA-Z_0-9\\_\\-]"){
            KeyValueValidator.findInvalidCharacterForInitializeKeyName("-key") should be (Some("-"))
            KeyValueValidator.findInvalidCharacterForInitializeKeyName("$name")should be (Some("$"))
        	KeyValueValidator.findInvalidCharacterForInitializeKeyName("@name")should be (Some("@"))
        	KeyValueValidator.findInvalidCharacterForInitializeKeyName("1name")should be (Some("1"))
        	KeyValueValidator.findInvalidCharacterForInitializeKeyName(".name")should be (Some("."))
        	KeyValueValidator.findInvalidCharacterForInitializeKeyName("áname")should be (Some("á"))
        	KeyValueValidator.findInvalidCharacterForInitializeKeyName("*name")should be (Some("*"))
        }
    }
    
    describe("#findInvalidCharacterForName"){
    	it("return all char that differ from [a-zA-Z_0-9\\_\\-]"){
        	KeyValueValidator.findInvalidCharacterForName("user name") should be (Some(" "))
        	KeyValueValidator.findInvalidCharacterForName("user.name") should be (Some("."))
        	KeyValueValidator.findInvalidCharacterForName("user@name") should be (Some("@"))
        	KeyValueValidator.findInvalidCharacterForName("n#me") should be (Some("#"))
        	KeyValueValidator.findInvalidCharacterForName("n%me") should be (Some("%"))
        	KeyValueValidator.findInvalidCharacterForName("nam&") should be (Some("&"))
        	KeyValueValidator.findInvalidCharacterForName("na(me") should be (Some("("))
        	KeyValueValidator.findInvalidCharacterForName("tést") should be (Some("é"))
        	KeyValueValidator.findInvalidCharacterForName("n@me") should be (Some("@"))
        	KeyValueValidator.findInvalidCharacterForName("n*me") should be (Some("*"))
    	}
    }
    
    describe("validateKey"){
    	it("throws an exception if there is no Key"){
            intercept[NoVariableNameException](KeyValueValidator.validateKey(""))
        }
        it("throws an exception if Key does not starts with a letter"){
        	//certo
        	KeyValueValidator.validateKey("_userName")
        	KeyValueValidator.validateKey("userName")
        	KeyValueValidator.validateKey("UserName")
        	
        	//errado
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("$name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("@name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("1name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey(".name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("áname"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("*name"))
        }
        it("throws an exception if Key contains space (' ') in its name"){
            intercept[InvalidVariableNameException](KeyValueValidator.validateKey("user name"))
        } 
        it("throws an exception if Key contains something differ from [a-zA-Z_0-9\\_\\-]"){
            //correto:
        	KeyValueValidator.validateKey("username")
        	KeyValueValidator.validateKey("userName")
        	KeyValueValidator.validateKey("UserName")
        	KeyValueValidator.validateKey("user_name")
        	KeyValueValidator.validateKey("user-name")
        	KeyValueValidator.validateKey("_userName")
            
            //errado:
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("user name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("user.name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("user@name"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("n#me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("n%me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("nam&"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("na(me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("tést"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("n@me"))
        	intercept[InvalidVariableNameException](KeyValueValidator.validateKey("n*me"))
        }
    }
    
    describe("Memorize.validate"){
        
    }

}
