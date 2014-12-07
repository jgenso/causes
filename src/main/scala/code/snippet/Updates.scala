package code
package snippet

import code.lib.snippet.PaginatorSnippet
import code.lib.util.DateHelper
import code.menu.CauseMenus
import code.model.{Cause, News}
import net.liftmodules.extras.SnippetHelper
import net.liftweb.http.S
import net.liftweb.json._
import net.liftweb.util.{PassThru, CssSel}
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

/**
 * Created by andrea on 12/7/14.
 */
object Updates extends SnippetHelper with PaginatorSnippet[News] {

  def page: List[News] = News.page(CauseMenus.causeMenu.currentValue openOr Cause.createRecord, curPage, itemsPerPage)

  def count: Long = News.count(CauseMenus.causeMenu.currentValue openOr Cause.createRecord)

  def list: CssSel = {
    "data-name=slogan *" #> (CauseMenus.causeMenu.currentValue openOr Cause.createRecord).slogan.get &
    "data-name=list *" #> page.map(news => {
      "data-name=register-date *" #> DateHelper.format(news.registerDate.get) &
      "data-name=title *" #> news.title.get &
      "data-name=description *" #> news.description.get &
      "data-name=photo" #> news.photo.photoHtml
    })
  }

}