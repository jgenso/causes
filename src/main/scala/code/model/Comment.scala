package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{DateField, ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.TextareaField

/**
 * Created by andrea on 12/6/14.
 */
//class Comment {
class Comment private() extends MongoRecord[Comment] with ObjectIdPk[Comment] {
  def meta = Comment

  object cause extends ObjectIdRefField(this, Cause)

  object user extends ObjectIdRefField(this, User)

  object comment extends TextareaField(this, 500) {
    override def displayName = "Comment"

    override def validations =
      valMaxLen(500, "Comment must be 500 characters or less") _ ::
        super.validations
  }

  object registerDate extends DateField(this){
    override def displayName = "Register date"
  }

  object parentComment extends ObjectIdRefField(this, Comment) {
    override def optional_? = true
  }

}

object Comment extends Comment with MongoMetaRecord[Comment] {

}

