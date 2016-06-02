import javax.inject.Inject

import org.databrary.PlayLogbackAccessFilter
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter

/**
  * `Filters` class in the root package is loaded automatically by Play.
  */
class Filters @Inject() (logback: PlayLogbackAccessFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(logback)
}
