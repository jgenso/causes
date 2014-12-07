package code
package menu

import code.config.MenuLoc
import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import model.{Cause, User}
import scala.xml._
import net.liftmodules.mongoauth.Locs

object CauseMenus extends Locs {

  val causeMenu =
    Menu.param[Cause]("Cause", "Cause", Cause.find(_), c => c.id.get.toString) / "cause" / * >>
      TemplateBox(() => Templates("cause" :: Nil))

  val causeStory =
    Menu.param[Cause]("Cause Story", "Cause Story", Cause.find(_), c => c.id.get.toString) /
      "cause" / * / "story" >> TemplateBox(() => Templates("cause" :: "story" :: Nil))

  val causeContributors =
    Menu.param[Cause]("Cause Contributors", "Cause Contributors", Cause.find(_), c => c.id.get.toString) /
      "cause" / * / "contributors" >> TemplateBox(() => Templates("cause" :: "contributors" :: Nil))

  val causeFollowers =
    Menu.param[Cause]("Cause Followers", "Cause Followers", Cause.find(_), c => c.id.get.toString) /
      "cause" / * / "followers" >> TemplateBox(() => Templates("cause" :: "followers" :: Nil))

  val causeLog =
    Menu.param[Cause]("Cause Log", "Cause Log", Cause.find(_), c => c.id.get.toString) / "cause" / * / "log" >>
      TemplateBox(() => Templates("cause" :: "log" :: Nil))

  val causeDashBoard =
    Menu.param[Cause]("Cause Dashboard", "Cause Dashboard", Cause.findForOrganizer(_, User.currentUser), c => c.id.get.toString) /
      "cause" / * / "dashboard" >> TemplateBox(() => Templates("cause" :: "dashboard" :: Nil))

  val causeComments =
    Menu.param[Cause]("Cause Comments", "Cause Comments", Cause.find(_), c => c.id.get.toString) /
      "cause" / * / "comments" >> TemplateBox(() => Templates("cause" :: "comments" :: Nil))

  val causeManagement =
    Menu("Cause Management", "Create cause") / "cause" / "add"  >> RequireLoggedIn

}
