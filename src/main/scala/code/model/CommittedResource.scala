package code
package model

import net.liftweb.common.Box
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}
import com.foursquare.rogue.LiftRogue._
import org.joda.time.DateTime
import org.bson.types.ObjectId

/**
 * Created by andrea on 12/6/14.
 */

class CommittedResource private() extends MongoRecord[CommittedResource] with ObjectIdPk[CommittedResource] {
  def meta = CommittedResource

  object  quantity extends IntField(this)

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

  object resource extends ObjectIdRefField(this, Resource)

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

  def findByCauseAndId(cause: Cause, id: String): Box[CommittedResource] = {
    CommittedResource.find(id).filter(_.cause.get == cause.id.get)
  }
}

object CommittedResourceStatus extends Enumeration {
  type CommittedResourceStatus  = Value
  val Committed, WaitingExecuted, Executed = Value
}
