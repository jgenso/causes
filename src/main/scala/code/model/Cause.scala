package code
package model

import net.liftweb.mongodb.record.field.{DateField, ObjectIdPk}
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.record.field._

/**
 * Created by andrea on 12/6/14.
 */

class Cause private() extends MongoRecord[Cause] with ObjectIdPk[Cause] {
  def meta = Cause

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

  object status extends EnumNameField(this, CauseStatus)

  object startCoorDate extends DateField(this) {
    override def displayName = "Start coordination date"
  }

  object endCoorDate extends DateField(this) {
    override def displayName = "End coordination date"
  }

  object startExeDate extends DateField(this){
    override def displayName = "Start execution date"
  }

  object endExeDate extends DateField(this){
    override def displayName = "End execution date"
  }

  object isInmedCoor extends BooleanField(this){
    override def displayName = "Inmediately coordination"
  }

  object isInmedExe extends  BooleanField(this){
    override def displayName = "Inmediately execution"
  }

  object registerDate extends DateField(this){
    override def displayName = "Register date"
  }

  object beneficiary extends StringField(this, 500){
    override  def displayName = "Benficiary"

    override def validations =
      valMaxLen(1000, "Beneficiary must be 500 characters or less") _ ::
        super.validations

  }

  object rate extends IntField(this)
}

object Cause extends Cause with MongoMetaRecord[Cause] {

}

object CauseStatus extends Enumeration {
  type CauseStatus  = Value
  val Active, Complete = Value
}