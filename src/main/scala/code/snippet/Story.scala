package code.snippet

import code.model.Cause
import net.liftweb.util
import scala.xml.NodeSeq
import util.Helpers._
/**
 * Created by andrea on 12/6/14.
 */

object Story {
  val cause: Cause = Cause.createRecord

  def render(html: NodeSeq) =  {
    "@name" #> cause.name.get &
    "@description" #> cause.description.get &
    "@photo" #> cause.photo.photoHtml
  }

}
