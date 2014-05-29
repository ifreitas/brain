package brain.models

import java.net.URL
import org.aitools.programd.Core
import org.aitools.programd.Bot
import brain.config.Config
import java.io.File
import org.apache.commons.io.FileUtils
import org.aitools.util.resource.URLTools
import java.io.FileInputStream
import java.io.FileOutputStream

object ProgramD {
    private var core:Core = null
    private var bot:Bot = null
    
    def prepare = {
        val programDDir = new File(Config.getProgramDDirPath)
        if(!programDDir.exists()){
            programDDir.mkdir()
            new File(Config.getProgramDConfPath).mkdir
            new File(Config.getProgramDDirPath+"/output").mkdir
            writeFiles
        }
    }
    def start(core:Core):Unit={
        this.core = core
        this.bot = core.getBots().getABot()
    }
	def response(message:String):String=core.getResponse(message, "brainUser", bot.getID)
	def shutdown:Unit= this.core.shutdown
	def restart():Unit={
	    core.shutdown()
	    core = new Core(core.getBaseURL(), URLTools.contextualize(core.getBaseURL(), Config.getProgramDCoreFilePath));
	}
	
	private def writeFiles()={
	    writeCore
	    writeBots
	    writeProperties
	    writeListeners
	    writePlugins
	    writePredicates
	    writeProperties
	    writeSentenceSplitters
	    writeSubstitutions
	}
	private def writeCore()={
	    writeFile(Config.getProgramDCoreFilePath, s"""<?xml version="1.0" encoding="UTF-8"?>
<programd xmlns="http://aitools.org/programd/4.7/programd-configuration"> 
  <aiml.namespace-uri>http://alicebot.org/2001/AIML-1.0.1</aiml.namespace-uri>
  <paths>
    <bot-config>bots.xml</bot-config>
    <plugin-config>plugins.xml</plugin-config>
    <gossip>file:${Config.getProgramDOutputPath}/gossip.txt</gossip>
  </paths>
  <predicates>
    <empty-default>undefined</empty-default>
    <client-name-predicate>name</client-name-predicate>
    <bot-name-property>name</bot-name-property>
    <predicate-flush-period>500</predicate-flush-period>
  </predicates>
  <predicate-manager>
    <implementation>org.aitools.programd.predicates.InMemoryPredicateManager</implementation>
    <ffpm-dir>file:${Config.getProgramDOutputPath}/ffpm</ffpm-dir>
  </predicate-manager>
  <database>
    <driver>com.mysql.jdbc.Driver</driver>
    <uri><![CDATA[jdbc:mysql://localhost:3306/programd?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true]]></uri>
    <username>yourusername</username>
    <password>yourpassword</password>
    <min-idle>30</min-idle>
    <max-active>70</max-active>
  </database>
  <merge>
    <policy>combine</policy>
    <note-each>true</note-each>
    <append-policy.separator-string> </append-policy.separator-string>
  </merge>
  <exceptions>
    <response-timeout>1000</response-timeout>
    <infinite-loop-input>INFINITE LOOP</infinite-loop-input>
    <on-uncaught-exceptions.print-stack-trace>true</on-uncaught-exceptions.print-stack-trace>
  </exceptions>
  <heart enabled="false">
    <pulse.implementation>org.aitools.programd.util.IAmAlivePulse</pulse.implementation>
    <pulse.rate>5</pulse.rate>
  </heart>
  <watchers>
    <AIML enabled="true">
      <timer>2000</timer>
    </AIML>
  </watchers>
  <interpreters>
    <javascript allowed="false">
      <interpreter-classname>org.aitools.programd.interpreter.RhinoInterpreter</interpreter-classname>
    </javascript>
    <system allowed="false">
      <directory>..</directory>
      <prefix/>
    </system>
  </interpreters>
  <loading>
    <category-load-notification-interval>1000</category-load-notification-interval>
    <note-each-loaded-file>true</note-each-loaded-file>
    <exit-immediately-on-startup>false</exit-immediately-on-startup>
  </loading>
  <connect-string>CONNECT</connect-string>
  <random-strategy>non-repeating</random-strategy>
  <graphmapper.implementation>org.aitools.programd.graph.MemoryGraphmapper</graphmapper.implementation>
  <nodemapper.implementation>org.aitools.programd.graph.TwoOptimalMemoryNodemapper</nodemapper.implementation>
  <use-shell>true</use-shell>
  <xml-parser>
    <catalog-path>resources/catalog.xml</catalog-path>
  </xml-parser>
</programd>""")
	}
	private def writeBots()={
	    writeFile(Config.getProgramDConfPath+"/bots.xml", s"""<?xml version="1.0" encoding="UTF-8"?>
<bots xmlns="http://aitools.org/programd/4.7/bot-configuration">
    <bot id="Brain Bot" enabled="true">
        <properties href="properties.xml"/>
        <predicates href="predicates.xml"/>
        <substitutions href="substitutions.xml"/>
        <sentence-splitters href="sentence-splitters.xml"/>
        <listeners href="listeners.xml"/>
        <learn>${Config.getKnowledgeBasePath}/*.aiml</learn>
    </bot>
</bots>""")
	}
	private def writePlugins()={
		writeFile(Config.getProgramDConfPath+"/plugins.xml", """<?xml version="1.0" encoding="UTF-8"?>
<plugins xmlns="http://aitools.org/programd/4.7/plugins"
    xmlns:d="http://aitools.org/programd/4.7">
</plugins>""")
	}
	private def writePredicates()={
		writeFile(Config.getProgramDConfPath+"/predicates.xml", """<?xml version="1.0" encoding="UTF-8"?>
<!--This is an example predicates set definition.  Here you can
    define default values for predicates, as well as mark those
    which are return-name-when-set.-->
<predicates xmlns="http://aitools.org/programd/4.7/bot-configuration">
    <predicate name="name" default="user" set-return="value"/>
    <predicate name="he" default="somebody" set-return="name"/>
    <predicate name="she" default="somebody" set-return="name"/>
    <predicate name="it" default="something" set-return="name"/>
    <predicate name="they" default="something" set-return="name"/>
    <!--To support test case 40.-->
    <predicate name="passed" default="" set-return="name"/>
    <predicate name="failed" default="" set-return="value"/>
</predicates>""")
	}
	private def writeProperties()={
		writeFile(Config.getProgramDConfPath+"/properties.xml", """<?xml version="1.0"?>
<!-- <properties xmlns="http://aitools.org/programd/4.7/bot-configuration"> -->
<properties>
      <!--This is an example properties set definition.-->
      <property name="name" value="YourBot"/>
      <property name="gender" value="bot-gender"/>
      <property name="master" value="bot-master"/>
      <property name="birthday" value="bot-birthday"/>
      <property name="birthplace" value="bot-birthplace"/>
      <property name="boyfriend" value="bot-boyfriend"/>
      <property name="favoritebook" value="bot-favoritebook"/>
      <property name="favoritecolor" value="bot-favoritecolor"/>
      <property name="favoriteband" value="bot-favoriteband"/>
      <property name="favoritefood" value="bot-favoritefood"/>
      <property name="favoritesong" value="bot-favoritesong"/>
      <property name="favoritemovie" value="bot-favoritemovie"/>
      <property name="forfun" value="bot-forfun"/>
      <property name="friends" value="bot-friends"/>
      <property name="girlfriend" value="bot-girlfriend"/>
      <property name="kindmusic" value="bot-kindmusic"/>
      <property name="location" value="bot-location"/>
      <property name="looklike" value="bot-looklike"/>
      <property name="question" value="bot-question"/>
      <property name="sign" value="bot-sign"/>
      <property name="talkabout" value="bot-talkabout"/>
      <property name="wear" value="bot-wear"/>
      <property name="website" value="bot-website"/>
      <property name="email" value="bot-email"/>
      <property name="language" value="bot-language"/>
</properties>""")
	}
	private def writeSentenceSplitters()={
		writeFile(Config.getProgramDConfPath+"/sentence-splitters.xml", """<?xml version="1.0" encoding="UTF-8"?>
<!--Sentence splitters define strings that mark the end of a sentence,
    after input substitutions have been performed.-->
<sentence-splitters xmlns="http://aitools.org/programd/4.7/bot-configuration">
    <!--NOTE THAT THESE WILL BE INTERPRETED AS REGULAR EXPRESSIONS!  So an unescaped "." means "any character", not "period".-->
    <splitter value="\."/>
    <splitter value="!"/>
    <splitter value="\?"/>
    <splitter value=";"/>
</sentence-splitters>""")
	}
	private def writeSubstitutions()={
		writeFile(Config.getProgramDConfPath+"/substitutions.xml", """<?xml version="1.0" encoding="UTF-8"?>
<!--Substitutions are grouped according to several AIML interpreter functions.-->
<substitutions xmlns="http://aitools.org/programd/4.7/bot-configuration">
    <!--Input substitutions correct spelling mistakes and convert
        "sentence"-ending characters into characters that will not be
        identified as sentence enders.-->
    <input>
        <substitute find="=reply" replace=""/>
        <substitute find="name=reset" replace=""/>
        <substitute find=":\-\)" replace=" smile "/>
        <substitute find=":\)" replace=" smile "/>
        <substitute find=",\)" replace=" smile "/>
        <substitute find=";\)" replace=" smile "/>
        <substitute find=";-\)" replace=" smile "/>
        <substitute find="&quot;" replace=""/>
        <substitute find="/" replace=" "/>
        <substitute find="&gt;" replace=" gt "/>
        <substitute find="&lt;" replace=" lt "/>
        <substitute find="\(" replace=" "/>
        <substitute find="\)" replace=" "/>
        <substitute find="`" replace=" "/>
        <substitute find="," replace=" "/>
        <substitute find=":" replace=" "/>
        <substitute find="&amp;" replace=" "/>
        <substitute find="\-" replace="-"/>
        <substitute find="=" replace=" "/>
        <substitute find="," replace=" "/>
        <substitute find="  " replace=" "/>
        <substitute find="\bl a\b" replace="la"/>
        <substitute find="\bo k\b" replace="ok"/>
        <substitute find="\bp s\b" replace="ps"/>
        <substitute find="\bohh" replace=" oh"/>
        <substitute find="\bhehe" replace=" he"/>
        <substitute find="\bhaha" replace=" ha"/>
        <substitute find="\bhellp\b" replace="help"/>
        <substitute find="\bbecuse\b" replace="because"/>
        <substitute find="\bbeleive\b" replace="believe"/>
        <substitute find="\bbecasue\b" replace="because"/>
        <substitute find="\bbecuase\b" replace="because"/>
        <substitute find="\bbecouse\b" replace="because"/>
        <substitute find="\bpractice\b" replace="practise"/>
        <substitute find="\breductionalism\b" replace="reductionism"/>
        <substitute find="\bloebner price\b" replace="loebner prize"/>
        <substitute find="\bits a\b" replace="it is a"/>
        <substitute find="\bnoi\b" replace="yes I"/>
        <substitute find="\bfav\b" replace="favorite"/>
        <substitute find="\byesi\b" replace="yes I"/>
        <substitute find="\byesit\b" replace="yes it"/>
        <substitute find="\biam\b" replace="I am"/>
        <substitute find="\bwelli\b" replace="well I"/>
        <substitute find="\bwellit\b" replace="well it"/>
        <substitute find="\bamfine\b" replace="am fine"/>
        <substitute find="\baman\b" replace="am an"/>
        <substitute find="\bamon\b" replace="am on"/>
        <substitute find="\bamnot\b" replace="am not"/>
        <substitute find="\brealy\b" replace="really"/>
        <substitute find="\biamusing\b" replace="I am using"/>
        <substitute find="\bamleaving\b" replace="am leaving"/>
        <substitute find="\byeah\b" replace="yes"/>
        <substitute find="\byep\b" replace="yes"/>
        <substitute find="\byha\b" replace="yes"/>
        <substitute find="\byuo\b" replace="you"/>
        <substitute find="\bwanna\b" replace="want to"/>
        <substitute find="\byou'd\b" replace="you would"/>
        <substitute find="\byou're\b" replace="you are"/>
        <substitute find="\byou re\b" replace="you are"/>
        <substitute find="\byou've\b" replace="you have"/>
        <substitute find="\byou ve\b" replace="you have"/>
        <substitute find="\byou'll\b" replace="you will"/>
        <substitute find="\byou ll\b" replace="you will"/>
        <substitute find="\byoure\b" replace="you are"/>
        <substitute find="\bdidnt\b" replace="did not"/>
        <substitute find="\bdidn't\b" replace="did not"/>
        <substitute find="\bdid'nt\b" replace="did not"/>
        <substitute find="\bcouldn't\b" replace="could not"/>
        <substitute find="\bcouldn t\b" replace="could not"/>
        <substitute find="\bdidn't\b" replace="did not"/>
        <substitute find="\bdidn t\b" replace="did not"/>
        <substitute find="\bain't\b" replace="is not"/>
        <substitute find="\bain t\b" replace="is not"/>
        <substitute find="\bisn't\b" replace="is not"/>
        <substitute find="\bisn t\b" replace="is not"/>
        <substitute find="\bisnt\b" replace="is not"/>
        <substitute find="\bit's\b" replace="it is"/>
        <substitute find="\bit s\b" replace="it is"/>
        <substitute find="\bare'nt\b" replace="are not"/>
        <substitute find="\barent\b" replace="are not"/>
        <substitute find="\baren't\b" replace="are not"/>
        <substitute find="\baren t\b" replace="are not"/>
        <substitute find="\barn t\b" replace="are not"/>
        <substitute find="\bwhere's\b" replace="where is"/>
        <substitute find="\bwhere s\b" replace="where is"/>
        <substitute find="\bhaven't\b" replace="have not"/>
        <substitute find="\bhavent\b" replace="have not"/>
        <substitute find="\bhasn't\b" replace="has not"/>
        <substitute find="\bhasn t\b" replace="has not"/>
        <substitute find="\bweren t\b" replace="were not"/>
        <substitute find="\bweren't\b" replace="were not"/>
        <substitute find="\bwerent\b" replace="were not"/>
        <substitute find="\bcan't\b" replace="can not"/>
        <substitute find="\bcan t\b" replace="can not"/>
        <substitute find="\bcant\b" replace="can not"/>
        <substitute find="\bcannot\b" replace="can not"/>
        <substitute find="\bwhos\b" replace="who is"/>
        <substitute find="\bhow's\b" replace="how is"/>
        <substitute find="\bhow s\b" replace="how is"/>
        <substitute find="\bhow'd\b" replace="how did"/>
        <substitute find="\bhow d\b" replace="how did"/>
        <substitute find="\bhows\b" replace="how is"/>
        <substitute find="\bwhats\b" replace="what is"/>
        <substitute find="\bname's\b" replace="name is"/>
        <substitute find="\bwho's\b" replace="who is"/>
        <substitute find="\bwho s\b" replace="who is"/>
        <substitute find="\bwhat's\b" replace="what is"/>
        <substitute find="\bwhat s\b" replace="what is"/>
        <substitute find="\bthat's\b" replace="that is"/>
        <substitute find="\bthere's\b" replace="there is"/>
        <substitute find="\bthere s\b" replace="there is"/>
        <substitute find="\btheres\b" replace="there is"/>
        <substitute find="\bthats\b" replace="that is"/>
        <substitute find="\bwhats\b" replace="what is"/>
        <substitute find="\bdoesn't\b" replace="does not"/>
        <substitute find="\bdoesn t\b" replace="does not"/>
        <substitute find="\bdoesnt\b" replace="does not"/>
        <substitute find="\bdon't\b" replace="do not"/>
        <substitute find="\bdon t\b" replace="do not"/>
        <substitute find="\bdont\b" replace="do not"/>
        <substitute find="\bdo nt\b" replace="do not"/>
        <substitute find="\bdo'nt\b" replace="do not"/>
        <substitute find="\bwon't\b" replace="will not"/>
        <substitute find="\bwont\b" replace="will not"/>
        <substitute find="\bwon t\b" replace="will not"/>
        <substitute find="\blet's\b" replace="let us"/>
        <substitute find="\bthey're\b" replace="they are"/>
        <substitute find="\bthey re\b" replace="they are"/>
        <substitute find="\bwasn't\b" replace="was not"/>
        <substitute find="\bwasn t\b" replace="was not"/>
        <substitute find="\bwasnt\b" replace="was not"/>
        <substitute find="\bhadn't\b" replace="had not"/>
        <substitute find="\bhadn t\b" replace="had not"/>
        <substitute find="\bwouldn't\b" replace="would not"/>
        <substitute find="\bwouldn t\b" replace="would not"/>
        <substitute find="\bwouldnt\b" replace="would not"/>
        <substitute find="\bshouldn't\b" replace="should not"/>
        <substitute find="\bshouldnt\b" replace="should not"/>
        <substitute find="\bfavourite\b" replace="favorite"/>
        <substitute find="\bcolour\b" replace="color"/>
        <substitute find="\bwe'll\b" replace="we will"/>
        <substitute find="\bwe ll\b" replace="we will"/>
        <substitute find="\bhe'll\b" replace="he will"/>
        <substitute find="\bhe ll\b" replace="he will"/>
        <substitute find="\bi'll\b" replace="I will"/>
        <substitute find="\bive\b" replace="I have"/>
        <substitute find="\bi've\b" replace="I have"/>
        <substitute find="\bi ve\b" replace="I have"/>
        <substitute find="\bi'd\b" replace="I would"/>
        <substitute find="\bi'm\b" replace="I am"/>
        <substitute find="\bi m\b" replace="I am"/>
        <substitute find="\bwe've\b" replace="we have"/>
        <substitute find="\bwe're\b" replace="we are"/>
        <substitute find="\bshe's\b" replace="she is"/>
        <substitute find="\bshes\b" replace="she is"/>
        <substitute find="\bshe'd\b" replace="she would"/>
        <substitute find="\bshe d\b" replace="she would"/>
        <substitute find="\bshed\b" replace="she would"/>
        <substitute find="\bhe'd\b" replace="he would"/>
        <substitute find="\bhe d\b" replace="he would"/>
        <substitute find="\bhed\b" replace="he would"/>
        <substitute find="\bhe's\b" replace="he is"/>
        <substitute find="\bwe ve\b" replace="we have"/>
        <substitute find="\bwe re\b" replace="we are"/>
        <substitute find="\bshe s\b" replace="she is"/>
        <substitute find="\bhe s\b" replace="he is"/>
        <substitute find="\biama\b" replace="I am a"/>
        <substitute find="\biamasking\b" replace="I am asking"/>
        <substitute find="\biamdoing\b" replace="I am doing"/>
        <substitute find="\biamfrom\b" replace="I am from"/>
        <substitute find="\biamin\b" replace="I am in"/>
        <substitute find="\biamok\b" replace="I am ok"/>
        <substitute find="\biamsorry\b" replace="I am sorry"/>
        <substitute find="\biamtalking\b" replace="I am talking"/>
        <substitute find="\biamtired\b" replace="I am tired"/>
        <substitute find="\bdown load\b" replace="download"/>
        <substitute find="\bremeber\b" replace="remember"/>
        <substitute find="\bwaht\b" replace="what"/>
        <substitute find="\bwallance\b" replace="wallace"/>
        <substitute find="\byou r\b" replace="you are"/>
        <substitute find="\bu\b" replace="you"/>
        <substitute find="\bur\b" replace="your"/>
        <!--sentence protection-->
        <substitute find="\{" replace=" beginscript "/>
        <substitute find="\}" replace=" endscript "/>
        <substitute find="\\" replace=" "/>
        <substitute find=":0" replace=" 0"/>
        <substitute find=": 0" replace=" 0"/>
        <substitute find=":1" replace=" 1"/>
        <substitute find=": 1" replace=" 1"/>
        <substitute find=":2" replace=" 2"/>
        <substitute find=": 2" replace=" 2"/>
        <substitute find=":3" replace=" 3"/>
        <substitute find=": 3" replace=" 3"/>
        <substitute find=":4" replace=" 4"/>
        <substitute find=": 4" replace=" 4"/>
        <substitute find=":5" replace=" 5"/>
        <substitute find=": 5" replace=" 5"/>
        <substitute find="\.0" replace=" point 0"/>
        <substitute find="\.1" replace=" point 1"/>
        <substitute find="\.2" replace=" point 3"/>
        <substitute find="\.4" replace=" point 4"/>
        <substitute find="\.5" replace=" point 5"/>
        <substitute find="\.6" replace=" point 6"/>
        <substitute find="\.7" replace=" point 7"/>
        <substitute find="\.8" replace=" point 8"/>
        <substitute find="\.9" replace=" point 9"/>
        <substitute find="\bdr\.\b" replace="Dr"/>
        <substitute find="\bdr\.w" replace=" Dr w"/>
        <substitute find="\bdr \.\b" replace="Dr"/>
        <substitute find="\bmr\.\b" replace="Mr"/>
        <substitute find="\bmrs\.\b" replace="Mrs"/>
        <substitute find="\bst\.\b" replace="St"/>
        <substitute find="\bwww\." replace=" www dot"/>
        <substitute find="\bbotspot\." replace=" botspot dot"/>
        <substitute find="\bamused\.com" replace=" amused dot com"/>
        <substitute find="\bwhatis\." replace=" whatis dot"/>
        <substitute find="\.com\b" replace="dot com"/>
        <substitute find="\.net\b" replace="dot net"/>
        <substitute find="\.org\b" replace="dot org"/>
        <substitute find="\.edu\b" replace="dot edu"/>
        <substitute find="\.uk\b" replace="dot uk"/>
        <substitute find="\.jp\b" replace="dot jp"/>
        <substitute find="\.au\b" replace="dot au"/>
        <substitute find="\.co\b" replace="dot co"/>
        <substitute find="\.ac\b" replace="dot ac"/>
        <substitute find="\bo\.k\.\b" replace="ok"/>
        <substitute find="\bo\. k\.\b" replace="ok"/>
        <substitute find="\bl\.l\.\b" replace="l l"/>
        <substitute find="\bp\.s\.\b" replace="ps"/>
        <substitute find="\balicebot\b" replace="ALICE"/>
        <substitute find="\ba l i c e\b" replace="ALICE"/>
        <substitute find="\ba\.l\.i\.c\.e\.\b" replace="ALICE"/>
        <substitute find="\ba\.l\.i\.c\.e\b" replace="ALICE"/>
        <substitute find="\bi\.c\.e\b" replace="i c e"/>
        <substitute find="\be l v i s\b" replace="ELVIS"/>
        <substitute find="\be\.l\.v\.i\.s\.\b" replace="ELVIS"/>
        <substitute find="\be\.l\.v\.i\.s\b" replace="ELVIS"/>
        <substitute find="\bv\.i\.s\b" replace="v i s"/>
        <substitute find="\bh a l\b" replace="hal"/>
        <substitute find="\bh\.a\.l\.\b" replace="hal"/>
        <substitute find="\bu s a\b" replace="USA"/>
        <substitute find="\bu\. s\. a\.\b" replace="USA"/>
        <substitute find="\bu\.s\.a\.\b" replace="USA"/>
        <substitute find="\bu\.s\.\b" replace="USA"/>
        <substitute find="\bph\.d\. " replace=" PhD"/>
        <substitute find="\ba\." replace="a"/>
        <substitute find="\bb\." replace="b"/>
        <substitute find="\bc\." replace="c"/>
        <substitute find="\bd\." replace="d"/>
        <substitute find="\be\." replace="e"/>
        <substitute find="\bf\." replace="f"/>
        <substitute find="\bg\." replace="g"/>
        <substitute find="\bh\." replace="h"/>
        <substitute find="\bi\." replace="i"/>
        <substitute find="\bj\." replace="j"/>
        <substitute find="\bk\." replace="k"/>
        <substitute find="\bl\." replace="l"/>
        <substitute find="\bm\." replace="m"/>
        <substitute find="\bn\." replace="n"/>
        <substitute find="\bp\." replace="p"/>
        <substitute find="\bo\." replace="o"/>
        <substitute find="\bq\." replace="q"/>
        <substitute find="\br\." replace="r"/>
        <substitute find="\bs\." replace="s"/>
        <substitute find="\bt\." replace="t"/>
        <substitute find="\bu\." replace="u"/>
        <substitute find="\bv\." replace="v"/>
        <substitute find="\bw\." replace="w"/>
        <substitute find="\bx\." replace="x"/>
        <substitute find="\by\." replace="y"/>
        <substitute find="\bz\." replace="z"/>
        <substitute find="\.jar" replace=" jar"/>
        <substitute find="\.zip" replace=" zip"/>
        <substitute find=", but " replace=".  "/>
        <substitute find=", and " replace=".  "/>
        <substitute find=",but " replace=".  "/>
        <substitute find=",and " replace=".  "/>
        <substitute find="  but " replace=".  "/>
        <substitute find="  and " replace=".  "/>
        <substitute find=", i " replace=".  I "/>
        <substitute find=", you " replace=".  you "/>
        <substitute find=",i " replace=".  I "/>
        <substitute find=",you " replace=".  you "/>
        <substitute find=", what " replace=".  what "/>
        <substitute find=",what " replace=".  what "/>
        <substitute find=", do " replace=".  do "/>
        <substitute find=",do " replace=".  do "/>
    </input>
    <gender>
        <substitute find="\bhe\b" replace="she"/>
        <substitute find="\bshe\b" replace="he"/>
        <substitute find="\bto him\b" replace="to her"/>
        <substitute find="\bfor him\b" replace="for her"/>
        <substitute find="\bwith him\b" replace="with her"/>
        <substitute find="\bon him\b" replace="on her"/>
        <substitute find="\bin him\b" replace="in her"/>
        <substitute find="\bto her\b" replace="to him"/>
        <substitute find="\bfor her\b" replace="for him"/>
        <substitute find="\bwith her\b" replace="with him"/>
        <substitute find="\bon her\b" replace="on him"/>
        <substitute find="\bin her\b" replace="in him"/>
        <substitute find="\bhis\b" replace="her"/>
        <substitute find="\bher\b" replace="his"/>
        <substitute find="\bhim\b" replace="her"/>
        <substitute find="\ber\b" replace="Sie"/>
        <substitute find="\bihm\b" replace="ihr"/>
        <substitute find="\bsein\b" replace="ihr"/>
        <substitute find="\bihn\b" replace="Sie"/>
    </gender>
    <person>
        <substitute find="\bI was\b" replace="he or she was"/>
        <substitute find="\bhe was\b" replace="I was"/>
        <substitute find="\bshe was\b" replace="I was"/>
        <substitute find="\bI am\b" replace="he or she is"/>
        <substitute find="\bI\b" replace="he or she"/>
        <substitute find="\bme\b" replace="him or her"/>
        <substitute find="\bmy\b" replace="his or her"/>
        <substitute find="\bmyself\b" replace="him or herself"/>
        <substitute find="\bmine\b" replace="his or hers"/>
    </person>
    <person2>
        <substitute find="\bwith you\b" replace="with me"/>
        <substitute find="\bwith me\b" replace="with you"/>
        <substitute find="\bto you\b" replace="to me"/>
        <substitute find="\bto me\b" replace="to you"/>
        <substitute find="\bof you\b" replace="of me"/>
        <substitute find="\bof me\b" replace="of you"/>
        <substitute find="\bfor you\b" replace="for me"/>
        <substitute find="\bfor me\b" replace="for you"/>
        <substitute find="\bgive you\b" replace="give me"/>
        <substitute find="\bgive me\b" replace="give you"/>
        <substitute find="\bgiving you\b" replace="giving me"/>
        <substitute find="\bgiving me\b" replace="giving you"/>
        <substitute find="\bgave you\b" replace="gave me"/>
        <substitute find="\bgave me\b" replace="gave you"/>
        <substitute find="\bmake you\b" replace="make me"/>
        <substitute find="\bmake me\b" replace="make you"/>
        <substitute find="\bmade you\b" replace="made me"/>
        <substitute find="\bmade me\b" replace="made you"/>
        <substitute find="\btake you\b" replace="take me"/>
        <substitute find="\btake me\b" replace="take you"/>
        <substitute find="\bsave you\b" replace="save me"/>
        <substitute find="\bsave me\b" replace="save you"/>
        <substitute find="\btell you\b" replace="tell me"/>
        <substitute find="\btell me\b" replace="tell you"/>
        <substitute find="\btelling you\b" replace="telling me"/>
        <substitute find="\btelling me\b" replace="telling you"/>
        <substitute find="\btold you\b" replace="told me"/>
        <substitute find="\btold me\b" replace="told you"/>
        <substitute find="\bare you\b" replace="am I"/>
        <substitute find="\bam I\b" replace="are you"/>
        <substitute find="\byou are\b" replace="I am"/>
        <substitute find="\bI am\b" replace="you are"/>
        <substitute find="\byou\b" replace="me"/>
        <substitute find="\bme\b" replace="you"/>
        <substitute find="\byour\b" replace="my"/>
        <substitute find="\bmy\b" replace="your"/>
        <substitute find="\byours\b" replace="mine"/>
        <substitute find="\bmine\b" replace="yours"/>
        <substitute find="\byourself\b" replace="myself"/>
        <substitute find="\bmyself\b" replace="yourself"/>
        <substitute find="\bI was\b" replace="you were"/>
        <substitute find="\byou were\b" replace="I was"/>
        <substitute find="\bI am\b" replace="you are"/>
        <substitute find="\byou are\b" replace="I am"/>
        <substitute find="\bI\b" replace="you"/>
        <substitute find="\bme\b" replace="you"/>
        <substitute find="\bmy\b" replace="your"/>
        <substitute find="\byour\b" replace="my"/>
        <substitute find="\bich war\b" replace="er war"/>
        <substitute find="\bich bin\b" replace="er ist"/>
        <substitute find="\bich\b" replace="er"/>
        <substitute find="\bmein\b" replace="sein"/>
        <substitute find="\bmeins\b" replace="seins"/>
        <substitute find="\bmit dir\b" replace="mit mir"/>
        <substitute find="\bdir\b" replace="mir"/>
        <substitute find="\bfuer dich\b" replace="fuer mich"/>
        <substitute find="\bbist du\b" replace="bin ich"/>
        <substitute find="\bdu\b" replace="ich"/>
        <substitute find="\bdein\b" replace="mein"/>
        <substitute find="\bdeins\b" replace="meins"/>
    </person2>
</substitutions>""")
	}
	private def writeListeners()={
		writeFile(Config.getProgramDConfPath+"/listeners.xml", """<?xml version="1.0" encoding="UTF-8"?>
<listeners xmlns="http://aitools.org/programd/4.7/bot-configuration"
    xmlns:d="http://aitools.org/programd/4.7">
    <listener class="org.aitools.programd.listener.AIMListener" enabled="false">
        <parameter name="userid" value="your-userid"/>
        <parameter name="password" value="your-password"/>
    </listener>
    <listener class="org.aitools.programd.listener.IRCListener" enabled="false">
        <parameter name="host" value="irc.freenode.net"/>
        <parameter name="port" value="6667"/>
        <parameter name="nick" value="your-nick"/>
        <parameter name="password" value="your-password"/>
        <parameter name="channel" value="#some-channel"/>
    </listener>
    <listener class="org.aitools.programd.listener.YahooListener" enabled="false">
        <parameter name="userid" value="your-userid"/>
        <parameter name="password" value="your-password"/>
    </listener>
</listeners>""")
	}
	
	private def writeFile(path:String, content:String):Unit={
		val file = new FileOutputStream(path)
		file.write(content.getBytes())
		file.flush()
        file.close()
	}
}