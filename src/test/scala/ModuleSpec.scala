
import org.databrary.{PlayLogbackAccessApi, PlayLogbackAccessFilter}
import org.scalatestplus.play._
import play.api.test.FakeApplication

class ModuleSpec extends PlaySpec {
  "PlayLogbackAccessModule" should {
    "load successfully" in {
      // Simply checking that Guice doesn't run into any
      // cyclic dependency errors.
      val app = FakeApplication()
      val api = app.injector.instanceOf[PlayLogbackAccessApi]
      val filter = app.injector.instanceOf[PlayLogbackAccessFilter]
      api must not be null
      filter must not be null
    }
  }
}
