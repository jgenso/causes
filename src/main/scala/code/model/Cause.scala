package code
package model

import com.mongodb.gridfs.GridFS
import net.liftweb.common.Full
import net.liftweb.http.{FileParamHolder, SHtml}
import net.liftweb.json.JsonAST.JObject
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.field.{MongoListField, DateField, ObjectIdPk}
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.record.field._
import net.liftweb.util.DefaultConnectionIdentifier
import org.bson.types.ObjectId
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import com.foursquare.rogue.LiftRogue._

import scala.xml.NodeSeq

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

  object slogan extends StringField(this, 100) {
    override def displayName = "Slogan"

    override def validations =
      valMaxLen(100, "Name must be 100 characters or less") _ ::
        super.validations
  }

  object status extends EnumNameField(this, CauseStatus)

  object startCoorDate extends DateField(this) {
    override def displayName = "Start coordination date"
  }

  object endCoorDate extends DateField(this) {
    override def displayName = "End coordination date"
  }

  object startExeDate extends DateField(this) {
    override def displayName = "Start execution date"
  }

  object endExeDate extends DateField(this) {
    override def displayName = "End execution date"
  }

  object isInmedCoor extends BooleanField(this) {
    override def displayName = "Inmediately coordination"
  }

  object isInmedExe extends  BooleanField(this) {
    override def displayName = "Inmediately execution"
  }

  object registerDate extends DateField(this) {
    override def displayName = "Register date"
  }

  object beneficiary extends StringField(this, 500) {
    override  def displayName = "Benficiary"

    override  def optional_? = true

    override def validations =
      valMaxLen(1000, "Beneficiary must be 500 characters or less") _ ::
        super.validations

  }

  object tags extends MongoListField[Cause, ObjectId](this)

  object rate extends IntField(this)

  object photo extends StringField(this, 500) {
    override def displayName = "Photo"
    private def photoHtml =
      <div class="image">
        <img class="img-responsive" src={s"/images/user/profile/${id.get}"} alt={s"${owner.name.get}'s photo"}/>
      </div>
    private def elem = {
      (value.trim match {
        case "" =>
          NodeSeq.Empty
        case other =>
          photoHtml
      }) ++
        SHtml.fileUpload(
          fph => {
            set(savePhoto(fph))
          }
        )
    }
    override def toForm = Full(elem)
    override def asHtml = photoHtml
    private def savePhoto(fph: FileParamHolder): String = {
      MongoDB.use(DefaultConnectionIdentifier) { db =>
        val fs = new GridFS(db)
        val mongoFile = fs.createFile(fph.fileStream, fph.fileName + " - " + org.apache.commons.codec.digest.DigestUtils.md5Hex(fph.fileStream))
        mongoFile.save()
        mongoFile.getFilename
      }
    }
  }
}

object Cause extends Cause with MongoMetaRecord[Cause] {

  override def asJValue(inst: Cause): JObject = {
    super.asJValue(inst) ~ ("resources" -> Resource.findAllByCause(inst).map(_.asJValue))
  }

  def isFollower(user: User, inst: Cause): Boolean = {
    CauseFollower.where(_.follower eqs user.id.get).and(_.cause eqs inst.id.get).count() > 0
  }

}

object CauseStatus extends Enumeration {
  type CauseStatus  = Value
  val Active, Complete, Canceled = Value
}