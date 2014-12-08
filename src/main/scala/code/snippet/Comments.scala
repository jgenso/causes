package code.snippet

import code.lib.snippet.PaginatorSnippet
import code.lib.util.DateHelper
import code.model.{User, Comment, Cause}
import net.liftmodules.extras.SnippetHelper
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import code.config.Site

import scala.xml.Text

/**
 * Created by andrea on 12/7/14.
 */

class Comments(cause: Cause) extends SnippetHelper with PaginatorSnippet[Comment] {

  def page: List[Comment] = Comment.page(cause, curPage, itemsPerPage)

  def count: Long = Comment.count(cause)

  def list: CssSel = {
      "data-name=list *" #> page.map(comments => {
        "data-name=register-date *" #> DateHelper.dateFormat.format(comments.registerDate.get) &
        "data-name=user *" #> SHtml.link(Site.profileLoc.calcHref(comments.user.obj.getOrElse(User.createRecord)),
          () => (), Text(comments.user.obj.getOrElse(User.createRecord).name.get)) &
        "data-name=photo-user *" #> comments.user.obj.getOrElse(User.createRecord).photo.asHtml  &
        "data-name=comment *" #> comments.comment.get
      })
  }

}
