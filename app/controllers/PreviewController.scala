package controllers

import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent._
import env.env
import play.api.libs.json._

class PreviewController @Inject() (ws:WSClient) extends Controller {

  def previewR() = Action.async{ implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
            data => {
              val ReportContent = data("reportContent").mkString
              val previewR = "PREVIEW888"  +  scala.util.Random.alphanumeric.take(10).mkString
              val path = "MarkDown/RMD/"  +  previewR
              import scala.sys.process._
              (s"mkdir $path").!
              scala.tools.nsc.io.File( path  + "/" + previewR + ".Rmd").writeAll(ReportContent)
              val dir = env.dir
              scala.io.Source.fromFile("previewR.R").getLines.
                       foreach { line => scala.tools.nsc.io.File( "MarkDown/Rshell/"  + previewR + ".R").
                               appendAll(line.replace("$fileR", previewR).replace("$dirR", dir) + sys.props("line.separator"))}
              import scala.sys.process._
              (s"R CMD BATCH MarkDown/Rshell/$previewR.R").!
              val host = env.host
              val url = "http://" + host + ":88/RMD/" + previewR + "/" +previewR + ".html"
              println("url is" + url)

              val json: JsValue = Json.obj(
                "data" -> url,
                "message" -> "预览成功!"
              )
              Future.successful(Ok(json))}
    }.getOrElse(Future.successful(Ok( "Error!!!" )))
  }


}





