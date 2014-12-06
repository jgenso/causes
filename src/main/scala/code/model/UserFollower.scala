package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, ObjectIdRefListField, ObjectIdPk}

/**
 * Created by andrea on 12/6/14.
 */
class UserFollower private() extends MongoRecord[UserFollower] with ObjectIdPk[UserFollower]{
  def meta = UserFollower

  object user extends ObjectIdRefField(this, User)
  object followers extends  ObjectIdRefListField(this, User)
}

object UserFollower extends UserFollower with MongoMetaRecord[UserFollower]{

}