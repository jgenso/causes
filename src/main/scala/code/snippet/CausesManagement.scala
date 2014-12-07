package code.snippet

import code.config.Site
import code.model.{Resource, Cause}
import net.liftweb.common.{Box, Full, Empty, Failure}
import net.liftweb.http.js.JsCmds.{Focus, RedirectTo, Noop}
import net.liftweb.http.{IdMemoizeTransform, RequestVar, S, SHtml}
import net.liftweb.util
import net.liftweb.util.CssSel
import util.Helpers._

import scala.collection.mutable.ArrayBuffer

/**
 * Created by andrea on 12/7/14.
 */

object resourcesRequestVar extends RequestVar[List[Resource]](Nil)
object resourceRequestVar extends RequestVar[Box[Resource]](Empty)

object CausesManagement {

  def add: CssSel = {
    val cause = Cause.createRecord
    "data-name=name" #> cause.name.toAjaxForm &
    "data-name=slogan" #> cause.slogan.toAjaxForm &
    "data-name=description" #> cause.description.toAjaxForm &
    "data-name=photo" #> cause.photo.toForm &
    "data-name=tags" #> cause.tags.toAjaxForm &
    "data-name=start-coord-date" #> cause.startCoorDate.toAjaxForm &
    "data-name=end-coord-date" #> cause.endCoorDate.toAjaxForm &
    "data-name=inmed-coord-date" #> cause.isInmedCoor.toForm &
    "data-name=start-exe-date" #> cause.startExeDate.toAjaxForm &
    "data-name=end-exe-date" #> cause.endExeDate.toAjaxForm &
    "data-name=inmed-exe-date" #> cause.isInmedExe.toForm &
    "data-name=country" #> cause.country.toForm &
    "data-name=location" #> cause.location.toAjaxForm &
    "data-name=item-memo" #> SHtml.idMemoize(items => {
      "data-name=items *" #> resourcesRequestVar.get.reverse.map(resource => {
        "data-name=resource-name" #> resource.name.toAjaxForm() &
        "data-name=resource-quantity" #> resource.quantity.toAjaxForm() &
        "data-name=resource-unit" #> resource.unit.toAjaxForm() &
        "data-name=remove-resource [onclick]" #> SHtml.ajaxInvoke(() => {
          resourcesRequestVar.set(resourcesRequestVar.is.filter(r => r != resource))
          items.setHtml() &
            Focus("add-resource-id")
          })
      }) &
      "data-name=add-resource [onclick]" #> SHtml.ajaxInvoke(() => addResourceJsCmd(items))

    }) &
    "type=submit" #> SHtml.ajaxOnSubmit(() => {
      cause.saveBox() match {
        case Empty => S.warning("Empty save")
        case Failure(msg, _, _) => S.error(msg)
        case Full(c: Cause) => {
          resourcesRequestVar.foreach(r => r.saveBox())
          RedirectTo(Site.home.url, () => S.notice("Cause saved"))
        }
      }
    })
  }

  def addResourceJsCmd(body: IdMemoizeTransform) = {
    val resource = Resource.createRecord

    resource.validate match {
      case Nil =>
        resourcesRequestVar.set(resource :: resourcesRequestVar.get)
        body.setHtml() &
          Focus("add-resource-id")
      case errors =>
        S.error(errors)
        Noop
    }
  }

}