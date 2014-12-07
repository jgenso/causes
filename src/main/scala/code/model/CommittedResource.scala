package code
package model

import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}
import com.foursquare.rogue.LiftRogue._
import org.joda.time.DateTime

/**
 * Created by andrea on 12/6/14.
 */

class CommittedResource private() extends MongoRecord[CommittedResource] with ObjectIdPk[CommittedResource] {
  def meta = CommittedResource

  object  quantity extends IntField(this)

  object unit extends StringField(this, 50) {
    override def displayName = "Unit"

    override def validations =
      valMaxLen(50, "Name must be 50 characters or less") _ ::
        super.validations
  }

  object registerDate extends DateField(this) {
    override def defaultValue = DateTime.now().toDate
    override def displayName = "Register date"
    override def asJValue: JValue = valueBox
      .map(v => JObject(JField("$dt", JString(formats.dateFormat.format(v))) :: Nil)) openOr (JNothing: JValue)
  }

  object status extends EnumNameField(this, CommittedResourceStatus) {
    override def defaultValue = CommittedResourceStatus.Committed
  }

  object cause extends ObjectIdRefField(this, Cause)

  object resource extends ObjectIdRefField(this, Resource) {
    override def asJValue: JValue = this.obj.map(_.asJValue) openOr (JNothing: JValue)
  }

  object joinedUser extends ObjectIdRefField(this, User) {
    override def asJValue: JValue = this.obj.map(_.asJValue) openOr (JNothing: JValue)
  }

}

object CommittedResource extends  CommittedResource with MongoMetaRecord[CommittedResource] {

  def findAllByCausePaginate(cause: Cause, page: Int, limit: Int = 10): List[CommittedResource] = {
    CommittedResource.where(_.cause eqs cause.id.get).paginate(limit).setPage(page).fetch()
  }

  def findAllByCause(cause: Cause): List[CommittedResource] = {
    CommittedResource.where(_.cause eqs cause.id.get).fetch()
  }

  def countAllByCause(cause: Cause): Long = {
    CommittedResource.where(_.cause eqs cause.id.get).count()
  }
}

object CommittedResourceStatus extends Enumeration {
  type CommittedResourceStatus  = Value
  val Committed, WaitingExecuted, Executed = Value
}

