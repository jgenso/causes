package code
package snippet

import code.menu.CauseMenus
import code.model.{User, Cause}
import net.liftweb.common.{Full, Empty, Failure}
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

/**
 * Created by andrea on 12/7/14.
 */
object Home {

  def lastCauses: CssSel = {
    "data-name=last-causes *" #> Cause.lastCauses.map(cause => {
      "data-name=name" #> cause.name.get &
      "data-name=photo" #> cause.photo.asHtml &
      "data-name=short-description" #> cause.description.get.take(60)
    })
  }

  def createCause: CssSel = {
    User.currentUser match {
      case Empty =>  "data-name=create-cause" #>  NodeSeq.Empty
      case Failure(msg, _, _) => "data-name=create-cause" #>  NodeSeq.Empty
      case Full(_) => "data-name=create-cause" #> SHtml.ajaxButton("Create cause",() =>
        RedirectTo(CauseMenus.causeManagement.toMenu.loc.calcDefaultHref))
    }
  }
}
