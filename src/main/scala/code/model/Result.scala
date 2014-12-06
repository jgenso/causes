package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{TextareaField, StringField}

/**
 * Created by andrea on 12/6/14.
 */
class Result private() extends MongoRecord[Result] with ObjectIdPk[Result] {
  def meta = Result

  object name extends StringField(this, 50) {
    override def displayName = "Name"

    override def validations =
      valMaxLen(50, "Name must be 50 characters or less") _ ::
        super.validations
  }

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

object Result extends Result with MongoMetaRecord[Result] {

}

