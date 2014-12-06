package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdPk, ObjectIdRefField}
import net.liftweb.record.field.BooleanField

/**
 * Created by andrea on 12/6/14.
 */

class CauseFollower private() extends MongoRecord[CauseFollower] with ObjectIdPk[CauseFollower]{
  def meta = CauseFollower

  object receiptSms extends BooleanField(this){
    override def displayName = "Receipt sms notification"
  }

  object receiptEmail extends  BooleanField(this){
    override def displayName = "Receipt email notificacion"
  }

  object cause extends ObjectIdRefField(this, Cause)
}

object CauseFollower extends CauseFollower with MongoMetaRecord[CauseFollower]{

}
