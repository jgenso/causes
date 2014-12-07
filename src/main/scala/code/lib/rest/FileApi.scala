package code
package lib
package rest


import com.mongodb.gridfs.{GridFSDBFile, GridFS}
import net.liftweb.common.Loggable
import net.liftweb.http.{NotFoundResponse, StreamingResponse, LiftResponse}
import net.liftweb.http.rest.RestHelper
import model._
import net.liftweb.mongodb.MongoDB
import net.liftweb.util.DefaultConnectionIdentifier

object FileApi extends RestHelper with Loggable {

  serve {
    case "images" :: "user"  :: "profile" :: AsUser(user) ::  Nil Get    req => serveFile(user.photo.get)
    case "images" :: "media" :: AsMedia(media) ::  Nil Get    req => serveFile(media.mediaFile.get)
    case "images" :: "cause" :: AsCause(cause) ::  Nil Get    req => serveFile(cause.photo.get)
    case "images" :: "news" :: AsNews(news) ::  Nil Get    req => serveFile(news.photo.get getOrElse "")
  }

  def serveFile(fileName: String): LiftResponse = {
    MongoDB.use(DefaultConnectionIdentifier) (db => {
      val fs = new GridFS(db)
      fs.findOne(fileName) match {
        case file: GridFSDBFile =>
          val fn = fileName.replace(" ","_")
          val headers =
            ("Content-type" -> file.getContentType) ::
              ("Content-length" -> file.getLength.toString) ::
              ("Content-disposition" -> (s"attachment; filename= $fn")) ::
              Nil
          StreamingResponse(
            file.getInputStream,
            () => file.getInputStream.close(),
            file.getLength,
            headers, Nil, 200)
        case _ =>
          NotFoundResponse("")
      }
    })
  }

}

object AsUser {
  def unapply(in: String): Option[User] = User.find(in)
}

object AsCause {
  def unapply(in: String): Option[Cause] = Cause.find(in)
}

object AsNews {
  def unapply(in: String): Option[News] = News.find(in)
}

object AsMedia {
  def unapply(in: String): Option[MediaFile] = MediaFile.find(in)
}