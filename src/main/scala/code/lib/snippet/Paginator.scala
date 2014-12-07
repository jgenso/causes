package code.lib.snippet

import net.liftweb.http.{PaginatorSnippet => LiftPaginatorSnippet, S}
import net.liftweb.util.{PassThru, CssSel}
import net.liftweb.util.Helpers._

import scala.xml.{Text, NodeSeq}

/**
 * Created by andrea on 12/7/14.
 */
trait PaginatorSnippet[T] extends LiftPaginatorSnippet[T] {

  lazy val baseURL = S.uri

  def offset: Int = (curPage - 1) * itemsPerPage

  override def firstXml: NodeSeq = Text(S ? "1")

  def firstLink: NodeSeq = {
    curPage match {
      case 1 => NodeSeq.Empty
      case _ => <li>{pageXml(1, firstXml)}</li>
    }
  }

  def prevLink: NodeSeq = {
    curPage match {
      case 1 => <li class="active"><a href="#">{prevXml}</a></li>
      case _ => <li>{pageXml(curPage-1 max 1, prevXml)}</li>
    }
  }

  def cleanLeft: CssSel = {
    curPage <= 4 match {
      case true => ".zoompagesleft" #> NodeSeq.Empty
      case false => ".zoompagesleft" #> PassThru
    }
  }

  def cleanRight: CssSel = {
    (curPage >= numPages-3)  match {
      case true => ".zoompagesright" #> NodeSeq.Empty
      case false => ".zoompagesright" #> PassThru
    }
  }

  def prevPages: List[Int] = curPage == numPages match {
    case true =>
      curPage - 3 to curPage - 1 filter (_ > 1) toList
    case _ =>
      curPage - 2 to curPage - 1 filter (_ > 1) toList
  }

  def nextPages: List[Int] = curPage match {
    case 1 =>
      curPage + 1 to curPage + 3 filter (_ > 1) filter (_ < numPages) toList
    case _ =>
      curPage + 1 to curPage + 2 filter (_ > 1) filter (_ < numPages) toList
  }

  def nextLink: NodeSeq = {
    (curPage >= numPages)  match {
      case true => <li class="active"><a href="#">{nextXml}</a></li>
      case false => <li>{pageXml(curPage + 1 min (numPages), nextXml)}</li>
    }
  }

  def lastLink: NodeSeq = {
    (curPage >= numPages)  match {
      case true => NodeSeq.Empty
      case false => <li>{pageXml(numPages, lastXml)}</li>
    }
  }

  def pagesXml(pages: Seq[Int]): NodeSeq ={
    pages map {n =>
      pageXml(n, Text(n toString))
    } match {
      case one :: Nil => one
      case first :: rest => rest.foldLeft(first) {
        case (a,b) => a ++ b
      }
      case Nil => Nil
    }
  }

  def paginate: CssSel = {
    ".pagination *" #> {
      ".first" #> firstLink &
        ".prev" #> prevLink &
        cleanLeft &
        ".allpages" #> {(n:NodeSeq) => pagesXml(0 until numPages, n)} &
        ".prevpages" #> pagesXml(prevPages).map(e => {
          ".page" #> e
        }) &
        ".active *" #> pageXml(curPage, currentXml) &
        ".nextpages" #> pagesXml(nextPages).map(e => {
          ".page" #> e
        }) &
        cleanRight &
        ".next" #> nextLink &
        ".last" #> lastLink &
        ".records" #> currentXml
    }
  }

}
