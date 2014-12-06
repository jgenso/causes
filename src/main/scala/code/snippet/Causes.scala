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
import org.bson.types.ObjectId

import scala.xml.NodeSeq

object Causes extends SnippetHelper {

  def render(in: NodeSeq): NodeSeq = {
    implicit val formats = DefaultFormats
    val elementId = "cause"

    def fetchCause() = {
      for {
        causeId <- CauseMenus.causeMenu.currentValue.map(_.id.get)
        cause   <- Cause.find(causeId)
      } yield {
        NgBroadcast(elementId, "after-fetch-cause", Full(cause.asJValue))
      }
    }

    def contribute(json: JValue) = {
      for {
        quantity <- tryo((json \ "quantity").extract[Int])
        resourceId <- tryo((json \ "resource").extract[ObjectId])
        cause <- CauseMenus.causeMenu.currentValue
        user <- User.currentUser
        cr <- createCommittedResource(quantity, resourceId, cause, user)
      } yield {
        NgBroadcast(elementId, "after-contribute", Full(cr.asJValue))
      }
    }

    def createCommittedResource(quantity: Int, resourceId: ObjectId, cause: Cause, user: User): Box[CommittedResource] = {
      val resource = CommittedResource.createRecord.quantity(quantity)
        .cause(cause.id.get).resource(resourceId).joinedUser(user.id.get)
      resource.saveBox()
    }

    val params: JValue =
      ("cause" -> CauseMenus.causeMenu.currentValue.map(_.asJValue))

    val funcs = JsObj(
      "fetchCause" -> JsExtras.AjaxCallbackAnonFunc(fetchCause),
      "contribute" -> JsExtras.JsonCallbackAnonFunc(contribute)
    )

    val onload =
      NgModule("ContributorsServer", Nil) ~>
        NgConstant("ServerParams", params) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
