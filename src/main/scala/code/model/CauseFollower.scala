package code
package model

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

  object follower extends ObjectIdRefField(this, User)
}

object CauseFollower extends CauseFollower with MongoMetaRecord[CauseFollower] {
  def findAllByCausePaginate(cause: Cause, page: Int, limit: Int = 10): List[CauseFollower] = {
    CauseFollower.where(_.cause eqs cause.id.get).paginate(limit).setPage(page).fetch()
  }

  def countAllByCause(cause: Cause): Long = {
    CauseFollower.where(_.cause eqs cause.id.get).count()
  }
}
