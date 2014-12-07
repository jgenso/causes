package code
package model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{EnumNameField, IntField, StringField}

/**
 * Created by andrea on 12/6/14.
 */

class CommittedResource private() extends MongoRecord[CommittedResource] with ObjectIdPk[CommittedResource] {
  def meta = CommittedResource

  object  quantity extends IntField(this)

  object registerDate extends DateField(this) {
    override def displayName = "Register date"
  }

  object status extends EnumNameField(this, CommittedResourceStatus)

  object cause extends ObjectIdRefField(this, Cause)

  object resource extends ObjectIdRefField(this, Resource)

  object joinedUser extends ObjectIdRefField(this, User)

}

object CommittedResource extends  CommittedResource with MongoMetaRecord[CommittedResource] {

}

object CommittedResourceStatus extends Enumeration {
  type CommittedResourceStatus  = Value
  val Committed, WaitingExecuted, Executed = Value
}
