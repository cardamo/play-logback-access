
import org.databrary.{PlayLogbackAccessApi, PlayLogbackAccessFilter}
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

class ModuleSpec extends PlaySpec with GuiceOneServerPerSuite {
  "PlayLogbackAccessModule" should {
    "load successfully" in {
      // Simply checking that Guice doesn't run into any
      // cyclic dependency errors.
      val app = fakeApplication()
      val api = app.injector.instanceOf[PlayLogbackAccessApi]
      val filter = app.injector.instanceOf[PlayLogbackAccessFilter]
      api must not be null
      filter must not be null
    }
  }
}
