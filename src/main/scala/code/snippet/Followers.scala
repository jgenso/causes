package code.snippet

import code.menu.CauseMenus
import code.model._
import net.liftmodules.extras.NgJE._
import net.liftmodules.extras.NgJsCmds._
import net.liftmodules.extras.{JsExtras, SnippetHelper}
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.http.js.JE.{AnonFunc, JsObj}
import net.liftweb.http.js.JsCmds._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

object Followers extends SnippetHelper {

  def render(in: NodeSeq): NodeSeq = {
    implicit val formats = DefaultFormats
    val elementId = "followers"
    def fetchPage(json: JValue) = {
      for {
        page <- tryo((json \ "page").extract[Int])
        cause <- CauseMenus.causeContributors.currentValue
      } yield {
        val items = CauseFollower.findAllByCausePaginate(cause, page)
        val count = CauseFollower.countAllByCause(cause)
        val res = ("count" -> count) ~ ("items" -> items.map(_.asJValue))

        NgBroadcast(elementId, "after-fetch-page", Full(res))
      }
    }

    val funcs = JsObj(
      "fetchPage" -> JsExtras.JsonCallbackAnonFunc(fetchPage)
    )

    val onload =
      NgModule("FollowersServer", Nil) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
