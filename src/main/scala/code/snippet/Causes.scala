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

class Causes(cause: Cause) extends SnippetHelper {

  def menus = {
    "data-name=updates [href]" #> CauseMenus.causeMenu.calcHref(cause) &
    "data-name=story [href]" #> CauseMenus.causeStory.calcHref(cause) &
    "data-name=contributors [href]" #> CauseMenus.causeContributors.calcHref(cause) &
    "data-name=followers [href]" #> CauseMenus.causeFollowers.calcHref(cause) &
    "data-name=comments [href]" #> CauseMenus.causeComments.calcHref(cause) &
    "data-name=log [href]" #> CauseMenus.causeLog.calcHref(cause) &
    dashboardMenuCss
  }

  def dashboardMenuCss = Cause.isOrganizer(cause, User.currentUser) match {
    case true => "data-name=dashboard [href]" #> CauseMenus.causeDashBoard.calcHref(cause)
    case false => "data-name=dashboard" #> NodeSeq.Empty

  }

  def render(in: NodeSeq): NodeSeq = {
    implicit val formats = DefaultFormats
    val elementId = "cause"

    def fetchCause() = {
      for {
        cause   <- Cause.find(cause.id.get)
      } yield {
        NgBroadcast(elementId, "after-fetch-cause", Full(cause.asJValue))
      }
    }

    def unfollow() = {
      for {
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
        resourceId <- tryo((json \ "resource").extract[String])
        resource <- Resource.find(resourceId)
        user <- User.currentUser
        cr <- createCommittedResource(quantity, resource, cause, user)
        inst <- Cause.find(cause.id.get)
      } yield {
        val res = ("cause" -> inst.asJValue)
        NgBroadcast(elementId, "after-contribute", Full(res))
      }
    }

    def addComment(json: JValue) = {
      for {
        text <- tryo((json \ "text").extract[String])
        user <- User.currentUser
        comment <- createComment(text, cause, user)
        inst <- Cause.find(cause.id.get)
      } yield {
        NgBroadcast(elementId, "after-add-comment", Empty)
      }
    }

    def addNews(json: JValue) = {
      for {
        title <- tryo((json \ "title").extract[String])
        description <- tryo((json \ "description").extract[String])
        user <- User.currentUser
        news <- createNews(title, description, cause, user)
        inst <- Cause.find(cause.id.get)
      } yield {
        News.broadcast(news, cause)
        NgBroadcast(elementId, "after-add-news", Empty)
      }
    }

    def createCommittedResource(quantity: Int, resource: Resource, cause: Cause, user: User): Box[CommittedResource] = {
      val cr = CommittedResource.createRecord.quantity(quantity)
        .cause(cause.id.get).resource(resource.id.get).joinedUser(user.id.get)
      cr.saveBox()
    }

    def createComment(text: String, cause: Cause, user: User): Box[Comment] = {
      val comment = Comment.createRecord.comment(text)
        .cause(cause.id.get).user(user.id.get)
      comment.saveBox()
    }

    def createNews(title: String, description: String, cause: Cause, user: User): Box[News] = {
      val news = News.createRecord.title(title).description(description)
        .cause(cause.id.get).user(user.id.get)
      news.saveBox()
    }

    def fetchContributorsPage(json: JValue) = {
      val elementId = "contributors"
      for {
        page <- tryo((json \ "page").extract[Int])
        itemsPerPage <- tryo((json \ "itemsPerPage").extract[Int])
      } yield {
        val items = CommittedResource.findAllByCausePaginate(cause, page, itemsPerPage)
        val count = CommittedResource.countAllByCause(cause)
        val res = ("count" -> count) ~ ("items" -> items.map(_.asJValue))

        NgBroadcast(elementId, "after-fetch-page", Full(res))
      }
    }

    def fetchFollowersPage(json: JValue) = {
      val elementId = "followers"
      for {
        page <- tryo((json \ "page").extract[Int])
        itemsPerPage <- tryo((json \ "itemsPerPage").extract[Int])
      } yield {
        val items = CauseFollower.findAllByCausePaginate(cause, page)
        val count = CauseFollower.countAllByCause(cause)
        val res = ("count" -> count) ~ ("items" -> items.map(_.asJValue))

        NgBroadcast(elementId, "after-fetch-page", Full(res))
      }
    }

    val params: JValue =
      ("cause" -> cause.asJValue) ~
        ("isLogged" -> User.currentUser.dmap(false)(s => true)) ~ // ToDo move this to another place available for all the app
        ("isOrganizer" -> Cause.isOrganizer(cause, User.currentUser)) ~
        ("isFollower" -> CauseMenus.causeMenu.currentValue.dmap(false)(cause => User.currentUser.dmap(false)(Cause.isFollower(_, cause))))

    val funcs = JsObj(
      "fetchCause" -> JsExtras.AjaxCallbackAnonFunc(fetchCause),
      "contribute" -> JsExtras.JsonCallbackAnonFunc(contribute),
      "follow" -> JsExtras.JsonCallbackAnonFunc(follow),
      "unfollow" -> JsExtras.AjaxCallbackAnonFunc(unfollow),
      "fetchContributorsPage" -> JsExtras.JsonCallbackAnonFunc(fetchContributorsPage),
      "fetchFollowersPage" -> JsExtras.JsonCallbackAnonFunc(fetchFollowersPage),
      "addComment" -> JsExtras.JsonCallbackAnonFunc(addComment),
      "addNews" -> JsExtras.JsonCallbackAnonFunc(addNews)
    )

    val onload =
      NgModule("CauseServer", Nil) ~>
        NgConstant("ServerParams", params) ~>
        NgFactory("ServerFuncs", AnonFunc(JsReturn(funcs)))

    S.appendGlobalJs(JsExtras.IIFE(onload))
    in
  }

}
