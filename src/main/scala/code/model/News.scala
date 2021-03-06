package code
package model

import code.config.SmtpMailer
import com.mongodb.gridfs.GridFS
import com.twilio.sdk.TwilioRestClient
import net.liftmodules.mongoauth.MongoAuth
import net.liftweb.common.{Logger, Full}
import net.liftweb.http.{FileParamHolder, SHtml}
import net.liftweb.mongodb.MongoDB
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdRefField, DateField, ObjectIdPk}
import net.liftweb.record.field.{OptionalStringField, TextareaField, StringField}
import net.liftweb.util.{Helpers, Props, DefaultConnectionIdentifier}
import com.foursquare.rogue.LiftRogue._
import org.apache.http.message.BasicNameValuePair
import org.joda.time.DateTime
import scala.xml.NodeSeq
import org.apache.http.NameValuePair

/**
 * Created by andrea on 12/6/14.
 */
class News private() extends MongoRecord[News] with ObjectIdPk[News] {
  def meta = News

  object title extends StringField(this, 64) {
    override def displayName = "Tittle"

    override def validations =
      valMaxLen(64, "Tittle must be 64 characters or less") _ ::
        super.validations
  }
  object description extends TextareaField(this, 1000) {
    override def displayName = "Description"

    override def validations =
      valMaxLen(1000, "Description must be 1000 characters or less") _ ::
        super.validations
  }

  object registerDate extends DateField(this) {
    override def defaultValue = DateTime.now.toDate
    override def displayName = "Register date"
  }

  object photo extends OptionalStringField(this, 500) {
    override def displayName = "Photo"
    def photoHtml =
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

  object user extends ObjectIdRefField(this, User)
}

object News extends News with MongoMetaRecord[News] with Logger {

  def page(cause: Cause, curPage: Int, itemsPerPage: Int): List[News] =
    News.where(_.cause eqs cause.id.get).paginate(itemsPerPage).setPage(curPage).fetch()

  def count(cause: Cause): Long = News.where(_.cause eqs cause.id.get).count()

  def broadcast(news: News, cause: Cause): Unit = {
    sendSms(news, cause)
    sendEmails(news, cause)
  }

  private def sendSms(news: News, cause: Cause) = Helpers.tryo {
    val accountSid = Props.get("twilio.accountid","")
    val authToken = Props.get("twilio.authtoken","")
    val twilioNumber = Props.get("twilio.number", "")
    val client = new TwilioRestClient(accountSid, authToken)
    val account = client.getAccount()
    val messageFactory = account.getMessageFactory()

    CauseFollower.findAllByCauseAndSmsNotification(cause).foreach(follower => {
      val params = new java.util.ArrayList[NameValuePair]()
      params.add(new BasicNameValuePair("To", follower.follower.obj.dmap("")(_.cellPhone.get)))
      params.add(new BasicNameValuePair("From", "+1 201-731-8427"))
      params.add(new BasicNameValuePair("Body", s"${cause.name.get} - ${news.title.get}: ${news.description.get}"));
      info(messageFactory.create(params).getStatus)
    })
  }

  private def sendEmails(news: News, cause: Cause) = Helpers.tryo {
    import net.liftweb.util.Mailer._

    CauseFollower.findAllByCauseAndEmailNotification(cause).foreach { follower =>

      val msgTxt = news.description.get.stripMargin

      sendMail(
        From(MongoAuth.systemFancyEmail),
        Subject("%s - %s".format(cause.name.get, news.title.get)),
        To(follower.follower.obj.dmap("")(_.email.get)),
        PlainMailBodyType(msgTxt)
      )
    }
  }

}