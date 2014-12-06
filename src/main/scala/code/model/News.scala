package code
package model

import code.model.Result.photo._
import com.mongodb.gridfs.GridFS
import net.liftweb.common.Full
import net.liftweb.http.{FileParamHolder, SHtml}
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, DateField, ObjectIdPk}
import net.liftweb.record.field.{OptionalStringField, TextareaField, StringField}
import net.liftweb.util.DefaultConnectionIdentifier

import scala.xml.NodeSeq

/**
 * Created by andrea on 12/6/14.
 */
class News private() extends MongoRecord[News] with ObjectIdPk[News] {
  def meta = News

  object description extends TextareaField(this, 1000) {
    override def displayName = "Description"

    override def validations =
      valMaxLen(1000, "Description must be 1000 characters or less") _ ::
        super.validations
  }

  object registerDate extends DateField(this) {
    override def displayName = "Register date"
  }

  object photo extends OptionalStringField(this, 500) {
    override def displayName = "Photo"
    private def photoHtml =
      <div class="image">
        <img class="img-responsive" src={s"/images/user/profile/${id.get}"} alt={s"${id.get}'s news photo"}/>
      </div>
    private def elem = {
      (value.headOption.map(v => v).getOrElse("").trim match {
        case "" =>
          NodeSeq.Empty
        case other =>
          photoHtml
      }) ++
        SHtml.fileUpload(
          fph => {
            set(Some(savePhoto(fph)))
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

  object cause extends ObjectIdRefField(this, Cause)

}

object News extends News with MongoMetaRecord[News] {

}