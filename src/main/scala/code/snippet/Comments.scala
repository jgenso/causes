package code.snippet

import code.lib.snippet.PaginatorSnippet
import code.lib.util.DateHelper
import code.model.{Comment, Cause}
import net.liftmodules.extras.SnippetHelper
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

/**
 * Created by andrea on 12/7/14.
 */
//object Comments(cause: Cause) extends SnippetHelper with PaginatorSnippet[Comments] {

  //def page: List[Comments] = Comment.page(cause, curPage, itemsPerPage)

  //def count: Long = Comment.count(cause)

 /* def list: CssSel = {
    "data-name=slogan *" #> cause.slogan.get &
      "data-name=list *" #> page.map(comments => {
        "data-name=register-date *" #> DateHelper.format(comments.registerDate.get) &
          "data-name=title *" #> comments.title.get &
          "data-name=description *" #> comments.description.get &
          "data-name=photo" #> comments.photo.photoHtml
      })
  }*/

//}
