package code
package snippet

import code.lib.snippet.PaginatorSnippet
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

  def page: List[News] = News.page(CauseMenus.causeMenu.currentValue openOr Cause.createRecord, offset, itemsPerPage)

  def count: Long = News.count(CauseMenus.causeMenu.currentValue openOr Cause.createRecord)

  def list: CssSel = {
    ".title *" #> S.?("Updates") &
      ".list" #> page.map(news => {
          ".register-date *" #> news.registerDate.formatted("dd/MM/yyyy") &
          ".tittle *" #> news.tittle.get &
          ".description *" #> news.description.get
          ".photo" #> news.photo.photoHtml
      })
  }

}