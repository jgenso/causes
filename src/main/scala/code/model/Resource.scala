package code
package model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{IntField, StringField}
import com.foursquare.rogue.LiftRogue._

/**
 * Created by andrea on 12/6/14.
 */

class Resource private() extends MongoRecord[Resource] with ObjectIdPk[Resource] {
  def meta = Resource

  object name extends StringField(this, 50) {
    override def displayName = "Name"

    override def validations =
      valMaxLen(50, "Name must be 50 characters or less") _ ::
        super.validations
  }

  object  quantity extends IntField(this)

  object unit extends StringField(this, 50) {
    override def displayName = "Unit"

    override def validations =
      valMaxLen(50, "Unit must be 50 characters or less") _ ::
        super.validations
  }

  object cause extends ObjectIdRefField(this, Cause)

}

object Resource extends  Resource with MongoMetaRecord[Resource] {

  def findAllByCause(cause: Cause): List[Resource] = {
    Resource.where(_.cause eqs cause.id.get).fetch()
  }

}
