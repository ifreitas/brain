package brain.functionaltest

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Suite
import org.scalatest.selenium.WebBrowser
import org.openqa.selenium.firefox.FirefoxDriver
import brain.web.JettyServer

trait WebApplication extends BeforeAndAfterAll { this: Suite =>
    val index = "http://localhost:8080"

    override def beforeAll() {
        if (!JettyServer.isRunning) {
            JettyServer.start
        }
        super.beforeAll() // To be stackable, must call super.beforeEach
    }

    override def afterAll(){
        try super.afterAll() // To be stackable, must call super.afterEach
        finally {
            JettyServer.stop
        }
    }
    
}

class MyBasicFunctionalTest(implicit webDriver:WebDriver) extends FlatSpec with Matchers with WebBrowser with BeforeAndAfter with WebApplication {
}