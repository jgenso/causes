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

    def unfollow() = {
      for {
        cause <- CauseMenus.causeMenu.currentValue
        user <- User.currentUser
      } yield {
        CauseFollower.deleteByCauseAndFollower(cause, user)
        val inst = Cause.find(cause.id.get)
        val res = ("isFollower" -> (inst.map(s => JBool(Cause.isFollower(user, cause))) openOr JBool(false))) ~
          ("cause" -> inst.map(_.asJValue))
        NgBroadcast(elementId, "after-unfollow", Full(res))
      }
    }

    def follow(json: JValue) = {
      for {
        allowSms <- tryo((json \ "sms").extract[Boolean])
        allowEmail <- tryo((json \ "email").extract[Boolean])
        cause <- CauseMenus.causeMenu.currentValue
        user <- User.currentUser
        cf <- CauseFollower.createRecord.receiptEmail(allowEmail).receiptSms(allowSms)
          .cause(cause.id.get).follower(user.id.get).saveBox()
      } yield {
        val inst = Cause.find(cause.id.get)
        val res = ("isFollower" -> (inst.map(s => JBool(Cause.isFollower(user, cause))) openOr JBool(false))) ~
          ("cause" -> inst.map(_.asJValue))
        NgBroadcast(elementId, "after-follow", Full(res))
      }
    }

    def contribute(json: JValue) = {
      for {
        quantity <- tryo((json \ "quantity").extract[Int])
        resourceId <- tryo((json \ "resource").extract[ObjectId])
        cause <- CauseMenus.causeMenu.currentValue
        user <- User.currentUser
        cr <- createCommittedResource(quantity, resourceId, cause, user)
        res <- Cause.find(cause.id.get)
      } yield {

        NgBroadcast(elementId, "after-contribute", Full(res.asJValue))
      }
    }

    def createCommittedResource(quantity: Int, resourceId: ObjectId, cause: Cause, user: User): Box[CommittedResource] = {
      val resource = CommittedResource.createRecord.quantity(quantity)
        .cause(cause.id.get).resource(resourceId).joinedUser(user.id.get)
      resource.saveBox()
    }

    val params: JValue =
      ("cause" -> CauseMenus.causeMenu.currentValue.map(_.asJValue)) ~
        ("isLogged" -> User.currentUser.dmap(false)(s => true)) ~ // ToDo move this to another place available for all the app
        ("isFollower" -> CauseMenus.causeMenu.currentValue.dmap(false)(cause => User.currentUser.dmap(false)(Cause.isFollower(_, cause))))

    val funcs = JsObj(
      "fetchCause" -> JsExtras.AjaxCallbackAnonFunc(fetchCause),
      "contribute" -> JsExtras.JsonCallbackAnonFunc(contribute),
      "follow" -> JsExtras.JsonCallbackAnonFunc(follow),
      "unfollow" -> JsExtras.AjaxCallbackAnonFunc(unfollow)
    )

    val onload =
      NgModule("CauseServer", Nil) ~>
        NgConstant("ServerParams", params) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
