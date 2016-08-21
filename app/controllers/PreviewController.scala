package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent._
import model.RForm
import env.env

class PreviewController @Inject() (ws:WSClient) extends Controller {

  def rpost() = Action.async { implicit request =>
    RForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("error!!!")),
      data => {
        val newU = data.rmd
        Future.successful(Ok(newU))
      }
    )
  }

  def previewR() = Action.async { implicit request =>
    RForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("error!!!")),
      data => {
        val Rmd = data.rmd

        val previewR = "PREVIEW888"  +  scala.util.Random.alphanumeric.take(10).mkString
        val path = "MarkDown/RMD/"  +  previewR

        import scala.sys.process._
        (s"mkdir $path").!

        scala.tools.nsc.io.File( path  + "/" + previewR + ".Rmd").writeAll(Rmd)

        val dir = env.dir

        scala.io.Source.fromFile("previewR.R").getLines.
          foreach { line => scala.tools.nsc.io.File( "MarkDown/Rshell/"  + previewR + ".R").
            appendAll(line.replace("$fileR", previewR).replace("$dirR", dir) + sys.props("line.separator"))}

        import scala.sys.process._
        (s"R CMD BATCH MarkDown/Rshell/$previewR.R").!

        val host = env.host

        println(s"http://$host:88/RMD/$previewR/$previewR.html")

        Logger.info(s"http://$host:88/RMD/$previewR/$previewR.html"  +  "   ++   this is the log testing")


        import scala.concurrent.ExecutionContext.Implicits.global   //这个引入包的作用在于隐身转换能够找到相应的执行环境！
        ws.url(s"http://$host:88/RMD/$previewR/$previewR.html").get().map {implicit response =>
          def responseBody = response.header(CONTENT_TYPE).filter(_.toLowerCase.contains("charset")).
                             fold(new String(response.body.getBytes("ISO-8859-1") , "UTF-8"))(_ => response.body)
          Ok(responseBody).as("text/html")
        }
      }
    )
  }





}





