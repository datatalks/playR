package controllers

import javax.inject.Inject
import env.env
import play.Logger
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent._

class Preview2Controller @Inject() (ws:WSClient) extends Controller {

  def preview() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
      data => {
        val ReportContent = data("reportContent").mkString
        val fileName = "PREVIEW_" + scala.util.Random.alphanumeric.take(10).mkString
        val path = "MarkDown/previewR/RMD/" + fileName
        import scala.sys.process._
        (s"mkdir $path").!
        scala.tools.nsc.io.File(path + "/" + fileName + ".Rmd").writeAll(ReportContent)
        val dir = env.dir
        scala.io.Source.fromFile("previewR.R").getLines.
          foreach { line => scala.tools.nsc.io.File("MarkDown/previewR/Rshell/" + fileName + ".R").
            appendAll(line.replace("$fileR", fileName).replace("$dirR", dir) + sys.props("line.separator"))
          }
        import scala.sys.process._
        (s"R CMD BATCH MarkDown/previewR/Rshell/$fileName.R").!
        val host = env.host
        val url = "http://" + host + "/previewR" + "/" + fileName
        println("url is" + url)
        val htmlContent = scala.io.Source.fromFile(s"MarkDown/previewR/RMD/$fileName/$fileName.html").mkString
        val json: JsValue = Json.obj(
          "data" -> htmlContent,
          "message" -> "预览成功!"
        )
        Future.successful(Ok(json))
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }

  def previewRhtml(fileName: String) = Action.async { implicit request =>
    val htmlContent = scala.io.Source.fromFile(s"MarkDown/previewR/RMD/$fileName/$fileName.html").mkString
    Logger.info(fileName + ".html has been responsed!!!")
    Future.successful(Ok(htmlContent).as(HTML))
  }

  def previewR() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
      data => {
        val ReportContent = data("reportContent").mkString
        val fileName = scala.util.Random.alphanumeric.take(10).mkString
        val path = "MarkDown/previewR/RMD/" + fileName
        import scala.sys.process._
        (s"mkdir $path").!
        scala.tools.nsc.io.File(path + "/" + fileName + ".Rmd").writeAll(ReportContent)
        val dir = env.dir
        scala.io.Source.fromFile("previewR.R").getLines.
          foreach { line => scala.tools.nsc.io.File("MarkDown/previewR/Rshell/" + fileName + ".R").
            appendAll(line.replace("$fileR", fileName).replace("$dirR", dir) + sys.props("line.separator"))
          }
        import scala.sys.process._
        (s"R CMD BATCH MarkDown/previewR/Rshell/$fileName.R").!
        val host = env.host
        val url = "http://" + host + "/previewR/" + fileName
        println("url is" + url)

        val json: JsValue = Json.obj(
          "data" -> url,
          "message" -> "预览成功!"
        )
        Future.successful(Ok(json))
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }


}





