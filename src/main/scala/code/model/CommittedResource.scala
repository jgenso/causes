package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}

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

  object registerDate extends DateField(this){
    override def displayName = "Register date"
  }

  object status extends EnumNameField(this, CommittesResourceStatus)

  object cause extends ObjectIdRefField(this, Cause)

  object resource extends ObjectIdRefField(this, Resource)

  object joinedUser extends ObjectIdRefField(this, User)

}

object CommittedResource extends  CommittedResource with MongoMetaRecord[CommittedResource] {

}

object CommittesResourceStatus extends Enumeration {
  type CommittedResourceStatus  = Value
  val Committed, WaitingExecuted, Executed = Value
}

