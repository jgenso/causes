package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, DateField, ObjectIdPk}
import net.liftweb.record.field.{TextareaField, StringField}

/**
 * Created by andrea on 12/6/14.
 */
class New private() extends MongoRecord[New] with ObjectIdPk[New] {
  def meta = New

  object description extends TextareaField(this, 1000) {
    override def displayName = "Description"

    override def validations =
      valMaxLen(1000, "Description must be 1000 characters or less") _ ::
        super.validations
  }

  object registerDate extends DateField(this){
    override def displayName = "Register date"
  }

  object cause extends ObjectIdRefField(this, Cause)

}

object New extends New with MongoMetaRecord[New] {

}