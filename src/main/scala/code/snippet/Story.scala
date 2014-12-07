package code.snippet

import code.menu.CauseMenus
import code.model.Cause
import net.liftweb.util
import util.Helpers._
/**
 * Created by andrea on 12/6/14.
 */

object Story {

  def image() = {
    val cause = CauseMenus.causeStory.currentValue openOr Cause.createRecord
    "data-name=photo *" #> cause.photo.photoHtml
  }
  def story() =  {
    val cause = CauseMenus.causeStory.currentValue openOr Cause.createRecord
    "data-name=description *" #> cause.description.get
  }

  def slogan() = {
    val cause = CauseMenus.causeStory.currentValue openOr Cause.createRecord
    "data-name=slogan *" #> cause.slogan.get
  }

  def name() = {
    val cause = CauseMenus.causeStory.currentValue openOr Cause.createRecord

    "data-name=name *" #> cause.name.get
  }

}
