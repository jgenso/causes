package code.snippet

import code.model.Cause
import net.liftweb.util
import scala.xml.NodeSeq
import util.Helpers._
/**
 * Created by andrea on 12/6/14.
 */

object Story {

  val cause: Cause

  def photo(xhtml: NodeSeq): NodeSeq = serve { cause =>
    <div id="cause-header">
      {cause.photo.photoHtml}
      <h3>{name(xhtml)}</h3>
    </div>
  }

  def story(html: NodeSeq): NodeSeq =  {

    "@name" #> cause.name.get &
    "@description" #> cause.description.get &
    "@photo" #>

  }



}
