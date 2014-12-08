package code.snippet

import code.lib.snippet.PaginatorSnippet
import code.lib.util.DateHelper
import code.model.{User, Comment, Cause}
import net.liftmodules.extras.SnippetHelper
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.{PassThru, CssSel}
import net.liftweb.util.Helpers._
import code.config.Site
import net.liftweb.common._

import scala.xml.Text

/**
 * Created by andrea on 12/7/14.
 */

class Comments(cause: Cause) extends SnippetHelper with PaginatorSnippet[Comment] {

  def page: List[Comment] = Comment.page(cause, curPage, itemsPerPage)

  def count: Long = Comment.count(cause)

  def list: CssSel = {
      "data-name=list *" #> page.map(comments => {
        avatar(comments.user.obj) &
        "data-name=register-date *" #> DateHelper.dateFormat.format(comments.registerDate.get) &
        "data-name=user *" #> SHtml.link(Site.profileLoc.calcHref(comments.user.obj.getOrElse(User.createRecord)),
          () => (), Text(comments.user.obj.getOrElse(User.createRecord).name.get)) &
        "data-name=photo-user *" #> comments.user.obj.getOrElse(User.createRecord).photo.asHtml  &
        "data-name=comment *" #> comments.comment.get
      })
  }

  def avatar(user: Box[User]) = user match {
    case Full(u) if u.photo.get.trim != "" =>
      "data-name=avatar [src]" #> s"/images/user/profile/${u.id.get}"
    case _ =>
      "*" #> PassThru
  }

}
