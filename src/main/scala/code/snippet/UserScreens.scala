package code
package snippet

import config.Site
import model._
import net.liftweb.http.js.JsCmds.RedirectTo

import scala.xml._

import net.liftweb._
import common._
import net.liftweb.http.{SHtml, LiftScreen, S}
import util.FieldError
import util.Helpers._

import net.liftmodules.extras.Gravatar

/*
 * Use for editing the currently logged in user only.
 */
sealed trait BaseCurrentUserScreen extends BaseScreen {
  object userVar extends ScreenVar(User.currentUser.openOr(User.createRecord))

  override def localSetup {
    Referer(Site.account.url)
  }
}

object AccountScreen extends BaseCurrentUserScreen {
  addFields(() => userVar.is.accountScreenFields)

  def finish() {
    userVar.is.saveBox match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(_) => S.notice("Account settings saved")
    }
  }
}

sealed trait BasePasswordScreen {
  this: LiftScreen =>

  def pwdName: String = "Password"
  def pwdMinLength: Int = 6
  def pwdMaxLength: Int = 32

  val passwordField = password(pwdName, "", trim,
    valMinLen(pwdMinLength, "Password must be at least "+pwdMinLength+" characters"),
    valMaxLen(pwdMaxLength, "Password must be "+pwdMaxLength+" characters or less"),
    "tabindex" -> "1"
  )
  val confirmPasswordField = password("Confirm Password", "", trim, "tabindex" -> "1")

  def passwordsMustMatch(): Errors = {
    if (passwordField.is != confirmPasswordField.is)
      List(FieldError(confirmPasswordField, "Passwords must match"))
    else Nil
  }
}


object PasswordScreen extends BaseCurrentUserScreen with BasePasswordScreen {
  override def pwdName = "New Password"
  override def validations = passwordsMustMatch _ :: super.validations

  def finish() {
    userVar.is.password(passwordField.is)
    userVar.is.password.hashIt
    userVar.is.saveBox match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(_) => S.notice("New password saved")
    }
  }
}

/*
 * Use for editing the currently logged in user only.
 */
object Profile {

  def render = {
    "*" #> User.currentUser.map(user => {
      "#photo" #> user.photo.toForm &
      "#name"  #> user.name.toForm &
      "#country" #> user.country.toForm &
      "#location" #> user.location.toForm &
      "#cellphone" #> user.cellPhone.toForm &
      "type=submit" #> SHtml.ajaxOnSubmit(() => {
        user.saveBox() match {
          case Empty => S.warning("Empty save")
          case Failure(msg, _, _) => S.error(msg)
          case Full(_) => RedirectTo(Site.account.url, () => S.notice("Profile settings saved"))
        }
      })
    })
  }
}

// this is needed to keep these fields and the password fields in the proper order
trait BaseRegisterScreen extends BaseScreen {
  object userVar extends ScreenVar(User.regUser.is)

  addFields(() => userVar.is.registerScreenFields)
}

/*
 * Use for creating a new user.
 */
object RegisterScreen extends BaseRegisterScreen with BasePasswordScreen {
  override def validations = passwordsMustMatch _ :: super.validations

  val rememberMe = builder("", User.loginCredentials.is.isRememberMe, ("tabindex" -> "1"))
    .help(Text("Remember me when I come back later."))
    .make

  override def localSetup {
    Referer(Site.home.url)
  }

  override def finishButton = super.finishButton % ("class" -> "gs-button")

  override def cancelButton = <input type="reset" class="gs-button"/>

  def finish() {
    val user = userVar.is
    user.password(passwordField.is)
    user.password.hashIt
    user.saveBox match {
      case Empty => S.warning("Empty save")
      case Failure(msg, _, _) => S.error(msg)
      case Full(u) =>
        User.logUserIn(u, true)
        if (rememberMe) User.createExtSession(u.id.get)
        S.notice("Thanks for signing up!")
    }
  }
}
