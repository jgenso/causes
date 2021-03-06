package code
package model

import code.lib.util.DateHelper
import com.mongodb.gridfs.GridFS
import net.liftweb.common.{Empty, Box, Full}
import net.liftweb.http.S.SFuncHolder
import net.liftweb.http.{S, FileParamHolder, SHtml}
import net.liftweb.json.JsonAST.JObject
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.field.{ObjectIdRefField, MongoListField, DateField, ObjectIdPk}
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.record.field._
import net.liftweb.util.DefaultConnectionIdentifier
import org.bson.types.ObjectId
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import com.foursquare.rogue.LiftRogue._
import net.liftweb.util.Helpers._

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

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object organizer extends ObjectIdRefField(this, User) {
    override def defaultValue = User.currentUser.dmap(User.createRecord.id.get)(_.id.get)
    override def asJValue: JValue = this.obj.dmap[JValue](JNothing)(s => s.asJValue)
  }

  object country extends CountryField(this) {
    override def displayName = "Country"
    override def asJValue: JValue = valueBox.dmap[JValue](JNothing)(s => JString(s.toString))
  }

  object location extends StringField(this, 32) {
    override def displayName = "Location"
    override def validations =
      valMaxLen(32, "Description must be 32 characters or less") _ ::
        super.validations

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object description extends TextareaField(this, 1000) {
    override def displayName = "Description"

    override def validations =
      valMaxLen(1000, "Description must be 1000 characters or less") _ ::
        super.validations

    def toAjaxForm() = SHtml.ajaxTextarea(this.value, v => {set(v.trim); Noop})
  }

  object slogan extends StringField(this, 100) {
    override def displayName = "Slogan"

    override def validations =
      valMaxLen(100, "Name must be 100 characters or less") _ ::
        super.validations

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object status extends EnumNameField(this, CauseStatus)

  object startCoorDate extends DateField(this) {
    override def displayName = "Start coordination date"
    override def defaultValue = java.util.Calendar.getInstance().getTime

    def toAjaxForm() = SHtml.ajaxText(DateHelper.dateFormat_yyyyMMdd.format(this.value),
      v => {
        val date = tryo(DateHelper.dateFormat_yyyyMMdd.parse(v)).getOrElse(defaultValue)
        set(date); Noop
      }, "type" -> "date")
  }

  object endCoorDate extends DateField(this) {
    override def displayName = "End coordination date"
    override def defaultValue = java.util.Calendar.getInstance().getTime
    def toAjaxForm() = SHtml.ajaxText(DateHelper.dateFormat_yyyyMMdd.format(this.value),
      v => {
        val date = tryo(DateHelper.dateFormat_yyyyMMdd.parse(v)).getOrElse(defaultValue)
        set(date); Noop
      }, "type" -> "date")
  }

  object startExeDate extends DateField(this) {
    override def displayName = "Start execution date"
    override def defaultValue = java.util.Calendar.getInstance().getTime
    def toAjaxForm() = SHtml.ajaxText(DateHelper.dateFormat_yyyyMMdd.format(this.value),
      v => {
        val date = tryo(DateHelper.dateFormat_yyyyMMdd.parse(v)).getOrElse(defaultValue)
        set(date); Noop
      }, "type" -> "date")
  }

  object endExeDate extends DateField(this) {
    override def displayName = "End execution date"
    override def defaultValue = java.util.Calendar.getInstance().getTime
    def toAjaxForm() = SHtml.ajaxText(DateHelper.dateFormat_yyyyMMdd.format(this.value),
      v => {
        val date = tryo(DateHelper.dateFormat_yyyyMMdd.parse(v)).getOrElse(defaultValue)
        set(date); Noop
      }, "type" -> "date")
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

    def toAjaxForm() = SHtml.ajaxText(this.value, v => {set(v.trim); Noop})
  }

  object tags extends MongoListField[Cause, String](this) {

    private def elem = {
      def elem0 = SHtml.ajaxText(this.value.mkString,
        v => {set(v.split(",").filter(v => v.trim.size > 0).map(v => v.trim).toList); Noop})

      SHtml.hidden(() => set(Nil)) ++ (uniqueFieldId match {
        case Full(id) => (elem0)
        case _ => elem0
      })
    }

    def toAjaxForm: Box[NodeSeq] =  Full(elem)
  }

  object rate extends IntField(this)

  object photo extends StringField(this, 500) {
    override def displayName = "Photo"
    def photoHtml =
      <div class="image">
        <img class="img-responsive" src={s"/images/cause/${id.get}"} alt={s"${owner.name.get}'s photo"}/>
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
    super.asJValue(inst) ~
      ("resources" -> Resource.findAllByCause(inst).map(_.asJValue)) ~
      ("committedResources" -> CommittedResource.findAllByCause(inst).map(_.asJValue)) ~
      ("totalContributors" -> CommittedResource.countAllByCause(inst)) ~
      ("totalFollowers" -> CauseFollower.countAllByCause(inst))
  }

  def isFollower(user: User, inst: Cause): Boolean = {
    CauseFollower.where(_.follower eqs user.id.get).and(_.cause eqs inst.id.get).count() > 0
  }

  def findForOrganizer(causeId: String, user: Box[User]): Box[Cause] = for {
    cause <- Cause.find(causeId)
    if user.map(_.id.get) === cause.organizer.get
  } yield cause

  def lastCauses: List[Cause] = {
    Cause.orderDesc(_.id).fetch()
  }

  def resourcesByCause(cause: Cause): List[Resource] = {
    Resource.where(_.cause eqs cause.id.get).fetch()
  }
  
  def isOrganizer(cause: Cause, user: Box[User]) = {
    user.map(_.id.get) === cause.organizer.get
  }

}

object CauseStatus extends Enumeration {
  type CauseStatus  = Value
  val Active, Complete, Canceled = Value
}