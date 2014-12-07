package code.snippet

import code.menu.CauseMenus
import code.model.Cause
import net.liftweb.util
import util.Helpers._
/**
 * Created by andrea on 12/6/14.
 */

class Story(cause: Cause) {

  def image = {
    "data-name=photo *" #> cause.photo.photoHtml
  }
  def story =  {
    "data-name=description *" #> cause.description.get
  }

  def slogan = {
    "data-name=slogan *" #> cause.slogan.get
  }

  def name = {
    "data-name=name *" #> cause.name.get
  }

}
