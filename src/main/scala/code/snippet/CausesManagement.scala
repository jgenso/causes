package code.snippet

import code.config.Site
import code.model.Cause
import net.liftweb.common.{Full, Empty, Failure}
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.http.{S, SHtml}
import net.liftweb.util
import net.liftweb.util.CssSel
import util.Helpers._

/**
 * Created by andrea on 12/7/14.
 */
object CausesManagement {

  def add: CssSel = {
    val cause = Cause.createRecord
    "data-name=name" #> cause.name.toForm &
    "data-name=slogan" #> cause.slogan.toForm &
    "data-name=description" #> cause.description.toForm &
    "data-name=photo" #> cause.photo.toForm &
    "data-name=tags" #> cause.tags.toForm &
    "type=submit" #> SHtml.ajaxOnSubmit(() => {
      cause.saveBox() match {
        case Empty => S.warning("Empty save")
        case Failure(msg, _, _) => S.error(msg)
        case Full(_) => RedirectTo(Site.home.url, () => S.notice("Cause saved"))
      }
    })
  }

}