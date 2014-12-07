package code
package model

import net.liftweb.http.SHtml
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{IntField, StringField}
import com.foursquare.rogue.LiftRogue._
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb. util.Helpers._

/**
 * Created by andrea on 12/6/14.
 */

class Resource private() extends MongoRecord[Resource] with ObjectIdPk[Resource] {
  def meta = Resource

  object name extends StringField(this, 50) {
    override def displayName = "Name"

    override def validations =
      valMaxLen(50, "Name must be 50 characters or less") _ ::
        super.validations

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object  quantity extends IntField(this) {
    def toAjaxForm() = SHtml.ajaxText(this.value.toString, v => {set(asInt(v).getOrElse(-1)); Noop})
  }

  object unit extends StringField(this, 50) {
    override def displayName = "Unit"

    override def validations =
      valMaxLen(50, "Unit must be 50 characters or less") _ ::
        super.validations

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object cause extends ObjectIdRefField(this, Cause)

}

object Resource extends  Resource with MongoMetaRecord[Resource] {
  def findByName(name: String): List[Resource] = {
    Resource.where(_.name eqs name).fetch()
  }
  
  def findAllByCause(cause: Cause): List[Resource] = {
    Resource.where(_.cause eqs cause.id.get).fetch()
  }
}
