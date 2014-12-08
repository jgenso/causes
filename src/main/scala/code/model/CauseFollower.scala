package code
package model

import net.liftweb.json.JsonAST.{JNothing, JValue}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefListField, ObjectIdPk, ObjectIdRefField}
import net.liftweb.record.field.BooleanField
import com.foursquare.rogue.LiftRogue._

/**
 * Created by andrea on 12/6/14.
 */

class CauseFollower private() extends MongoRecord[CauseFollower] with ObjectIdPk[CauseFollower]{
  def meta = CauseFollower

  object receiptSms extends BooleanField(this) {
    override def displayName = "Receipt sms notification"
  }

  object receiptEmail extends  BooleanField(this) {
    override def displayName = "Receipt email notificacion"
  }

  object cause extends ObjectIdRefField(this, Cause)

  object follower extends ObjectIdRefField(this, User) {
    override def asJValue: JValue = this.obj.dmap[JValue](JNothing)(_.asJValue)
  }
}

object CauseFollower extends CauseFollower with MongoMetaRecord[CauseFollower] {
  def findAllByCausePaginate(cause: Cause, page: Int, limit: Int = 10): List[CauseFollower] = {
    CauseFollower.where(_.cause eqs cause.id.get).paginate(limit).setPage(page).fetch()
  }

  def countAllByCause(cause: Cause): Long = {
    CauseFollower.where(_.cause eqs cause.id.get).count()
  }

  def deleteByCauseAndFollower(cause: Cause, follower: User) = {
    CauseFollower.where(_.cause eqs cause.id.get)
      .and(_.follower eqs follower.id.get)
      .foreach(cf => CauseFollower.delete_!(cf))
  }

  def findAllByCauseAndEmailNotification(cause: Cause): List[CauseFollower] = {
    CauseFollower.where(_.cause eqs cause.id.get).and(_.receiptEmail eqs true).fetch()
  }

  def findAllByCauseAndSmsNotification(cause: Cause): List[CauseFollower] = {
    CauseFollower.where(_.cause eqs cause.id.get).and(_.receiptSms eqs true).fetch()
  }
}
