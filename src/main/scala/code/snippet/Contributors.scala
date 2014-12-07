package code
package snippet

import code.menu.CauseMenus
import net.liftmodules.extras.{JsExtras, SnippetHelper}
import net.liftweb.http.{S, RequestVar}
import scala.xml.NodeSeq
import net.liftweb.common._
import net.liftweb.http.js.JE.{JsObj, AnonFunc}
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers
import net.liftmodules.extras.NgJE._
import net.liftmodules.extras.NgJsCmds._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.js.JsCmds._
import code.model._
import Helpers._

object Contributors extends SnippetHelper {

  def render(in: NodeSeq): NodeSeq = {
    implicit val formats = DefaultFormats
    val elementId = "contributors"
    def fetchPage(json: JValue) = {
      for {
        page <- tryo((json \ "page").extract[Int])
        cause <- CauseMenus.causeContributors.currentValue
      } yield {
        val items = CommittedResource.findAllByCausePaginate(cause, page)
        val count = CommittedResource.countAllByCause(cause)
        val res = ("count" -> count) ~ ("items" -> items.map(_.asJValue))

        NgBroadcast(elementId, "after-fetch-page", Full(res))
      }
    }

    val funcs = JsObj(
      "fetchPage" -> JsExtras.JsonCallbackAnonFunc(fetchPage)
    )

    val onload =
      NgModule("ContributorsServer", Nil) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
