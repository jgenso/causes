package code
package model

import com.mongodb.gridfs.GridFS
import net.liftweb.common.Full
import net.liftweb.http.{SHtml, FileParamHolder}
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, ObjectIdPk}
import net.liftweb.record.field.{TextareaField, StringField}
import net.liftweb.util.DefaultConnectionIdentifier

import scala.xml.NodeSeq

/**
 * Created by andrea on 12/6/14.
 */
class MediaFile private() extends MongoRecord[MediaFile] with ObjectIdPk[MediaFile] {
  def meta = MediaFile

  object tittle extends StringField(this, 50) {
    override def displayName = "Tittle"

    override def optional_? = true
  }

  object description extends StringField(this, 500) {
    override def displayName = "Description"

    override def optional_? = true
  }

  object mediaFile extends StringField(this, 500) {
    override def displayName = "Photo"
    private def photoHtml =
      <div class="image">
        <img class="img-responsive" src={s"/images/user/profile/${id.get}"} alt={s"${owner.tittle.get}'s photo"}/>
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

  object albumLogBook extends ObjectIdRefField(this, AlbumLogBook)
}

object MediaFile extends MediaFile with MongoMetaRecord[MediaFile] {

}