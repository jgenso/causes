package code
package model

import com.mongodb.gridfs.GridFS
import lib.RogueMetaRecord
import net.liftweb
import net.liftweb.mongodb.MongoDB

import org.bson.types.ObjectId
import org.joda.time.DateTime

import net.liftweb._
import common._
import http.{StringField => _, BooleanField => _, _}
import mongodb.record.field._
import record.field._
import net.liftweb.util.{DefaultConnectionIdentifier, FieldContainer}

import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.field._
import net.liftmodules.mongoauth.model._
import scala.xml._

class User private () extends ProtoAuthUser[User] with ObjectIdPk[User] {
  def meta = User

  def userIdAsString: String = id.toString

  object locale extends LocaleField(this) {
    override def displayName = "Locale"
    override def defaultValue = "en_US"
  }
  object timezone extends TimeZoneField(this) {
    override def displayName = "Time Zone"
    override def defaultValue = "America/Chicago"
  }

  object name extends StringField(this, 64) {
    override def displayName = "Name"

    override def validations =
      valMaxLen(64, "Name must be 64 characters or less") _ ::
      super.validations
  }
  object country extends CountryField(this) {
    override def displayName = "Country"
  }
  object location extends StringField(this, 64) {
    override def displayName = "Location"

    override def validations =
      valMaxLen(64, "Location must be 64 characters or less") _ ::
      super.validations
  }
  object cellPhone extends StringField(this,50) {
    override def displayName = "Cell Phone"
  }

  object photo extends StringField(this, 500) {
    override def displayName = "Photo"
    def photoHtml =
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

  /*
   * FieldContainers for various LiftScreeens.
   */
  def accountScreenFields = new FieldContainer {
    def allFields = List(username, email, locale, timezone)
  }

  def profileScreenFields = new FieldContainer {
    def allFields = List(photo, name, country, cellPhone, location)
  }

  def registerScreenFields = new FieldContainer {
    def allFields = List(name, username, email, country, cellPhone)
  }

  def whenCreated: DateTime = new DateTime(id.get.getDate)
}

object User extends User with ProtoAuthUserMeta[User] with RogueMetaRecord[User] with Loggable {
  import mongodb.BsonDSL._

  override def collectionName = "user.users"

  createIndex((email.name -> 1), true)
  createIndex((username.name -> 1), true)

  def findByEmail(in: String): Box[User] = find(email.name, in)
  def findByUsername(in: String): Box[User] = find(username.name, in)

  def findByStringId(id: String): Box[User] =
    if (ObjectId.isValid(id)) find(new ObjectId(id))
    else Empty

  override def onLogIn: List[User => Unit] = List(user => User.loginCredentials.remove())
  override def onLogOut: List[Box[User] => Unit] = List(
    x => logger.debug("User.onLogOut called."),
    boxedUser => boxedUser.foreach { u =>
      ExtSession.deleteExtCookie()
    }
  )

  /*
   * MongoAuth vars
   */
  private lazy val siteName = MongoAuth.siteName.vend
  private lazy val sysUsername = MongoAuth.systemUsername.vend
  private lazy val indexUrl = MongoAuth.indexUrl.vend
  private lazy val registerUrl = MongoAuth.registerUrl.vend
  private lazy val loginTokenAfterUrl = MongoAuth.loginTokenAfterUrl.vend

  /*
   * LoginToken
   */
  override def handleLoginToken: Box[LiftResponse] = {
    val resp = S.param("token").flatMap(LoginToken.findByStringId) match {
      case Full(at) if (at.expires.isExpired) => {
        at.delete_!
        RedirectWithState(indexUrl, RedirectState(() => { S.error("Login token has expired") }))
      }
      case Full(at) => find(at.userId.get).map(user => {
        if (user.validate.length == 0) {
          user.verified(true)
          user.update
          logUserIn(user)
          at.delete_!
          RedirectResponse(loginTokenAfterUrl)
        }
        else {
          at.delete_!
          regUser(user)
          RedirectWithState(registerUrl, RedirectState(() => { S.notice("Please complete the registration form") }))
        }
      }).openOr(RedirectWithState(indexUrl, RedirectState(() => { S.error("User not found") })))
      case _ => RedirectWithState(indexUrl, RedirectState(() => { S.warning("Login token not provided") }))
    }

    Full(resp)
  }

  // send an email to the user with a link for logging in
  def sendLoginToken(user: User): Unit = {
    import net.liftweb.util.Mailer._

    LoginToken.createForUserIdBox(user.id.get).foreach { token =>

      val msgTxt =
        """
          |Someone requested a link to change your password on the %s website.
          |
          |If you did not request this, you can safely ignore it. It will expire 48 hours from the time this message was sent.
          |
          |Follow the link below or copy and paste it into your internet browser.
          |
          |%s
          |
          |Thanks,
          |%s
        """.format(siteName, token.url, sysUsername).stripMargin

      sendMail(
        From(MongoAuth.systemFancyEmail),
        Subject("%s Password Help".format(siteName)),
        To(user.fancyEmail),
        PlainMailBodyType(msgTxt)
      )
    }
  }

  /*
   * ExtSession
   */
  def createExtSession(uid: ObjectId): Box[Unit] = ExtSession.createExtSessionBox(uid)

  /*
  * Test for active ExtSession.
  */
  def testForExtSession: Box[Req] => Unit = {
    ignoredReq => {
      if (currentUserId.isEmpty) {
        ExtSession.handleExtSession match {
          case Full(es) => find(es.userId.get).foreach { user => logUserIn(user, false) }
          case Failure(msg, _, _) =>
            logger.warn("Error logging user in with ExtSession: %s".format(msg))
          case Empty =>
        }
      }
    }
  }

  // used during login process
  object loginCredentials extends SessionVar[LoginCredentials](LoginCredentials(""))
  object regUser extends SessionVar[User](createRecord.email(loginCredentials.is.email))

}

case class LoginCredentials(email: String, isRememberMe: Boolean = false)
