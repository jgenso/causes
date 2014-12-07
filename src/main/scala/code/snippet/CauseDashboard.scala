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
import net.liftweb.util.Helpers._
import org.bson.types.ObjectId

import scala.xml.NodeSeq

object CauseDashboard extends SnippetHelper {

  def render(in: NodeSeq): NodeSeq = {
    implicit val formats = DefaultFormats
    val elementId = "causedashboard"

    def fetchCause() = {
      for {
        causeId <- CauseMenus.causeMenu.currentValue.map(_.id.get)
        cause   <- Cause.find(causeId)
      } yield {
        NgBroadcast(elementId, "after-fetch-cause", Full(cause.asJValue))
      }
    }

    def approve(json: JValue) = {
      for {
        resourceId <- tryo((json \ "resource").extract[String])
        cause <- CauseMenus.causeDashBoard.currentValue
        user <- User.currentUser
        cr <- CommittedResource.findByCauseAndId(cause, resourceId)
      } yield {
        val res = cr.status(CommittedResourceStatus.Executed).saveBox()
        NgBroadcast(elementId, "after-approve", res.map(_.asJValue))
      }
    }

    def cancel(json: JValue) = {
      for {
        resourceId <- tryo((json \ "resource").extract[String])
        cause <- CauseMenus.causeDashBoard.currentValue
        user <- User.currentUser
        cr <- CommittedResource.findByCauseAndId(cause, resourceId)
      } yield {
        val res = cr.deleteBox_!
        NgBroadcast(elementId, "after-cancel", Full(cr.asJValue))
      }
    }

    val params: JValue =
      ("cause" -> CauseMenus.causeDashBoard.currentValue.map(_.asJValue))

    val funcs = JsObj(
      "fetchCause" -> JsExtras.AjaxCallbackAnonFunc(fetchCause),
      "approve" -> JsExtras.JsonCallbackAnonFunc(approve),
      "cancel" -> JsExtras.JsonCallbackAnonFunc(cancel)
    )

    val onload =
      NgModule("CauseDashboardServer", Nil) ~>
        NgConstant("ServerParams", params) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
