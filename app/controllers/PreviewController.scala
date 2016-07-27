package controllers


import play.api.mvc._
import scala.concurrent._

import model.{UForm,UFormData}


class PreviewController extends Controller {

  def rpost() = Action.async { implicit request =>
    UForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("error!!!")),
      data => {
        val newU = data.name + data.password
        Future.successful(Ok(newU))
      }
    )
  }

  def rpost2() = Action.async { implicit request =>
    UForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("error!!!")),
      data => {
        val Rmd = data.name

        val previewR = "PREVIEW$$$"  +  scala.util.Random.alphanumeric.take(10).mkString

        scala.tools.nsc.io.File( "MarkDown/RMD/"  +  previewR + ".Rmd").writeAll(Rmd)

        scala.io.Source.fromFile("previewR.R").getLines.
          foreach { line => scala.tools.nsc.io.File( "MarkDown/Rshell/"  + previewR + ".R").
            appendAll(line.replace("$fileR", previewR) + sys.props("line.separator"))}

        import scala.sys.process._
        (s"R CMD BATCH MarkDown/Rshell/$previewR.R").!

        println(s"http://localhost:88/RMD/$previewR.html")

        import play.api.libs.ws._
        import play.api.Play.current
        import scala.concurrent.ExecutionContext.Implicits.global   //这个引入包的作用在于隐身转换能够找到相应的执行环境！
        WS.url(s"http://localhost:88/RMD/$previewR.html").get().map {implicit response =>
          def responseBody = response.header(CONTENT_TYPE).filter(_.toLowerCase.contains("charset")).
                             fold(new String(response.body.getBytes("ISO-8859-1") , "UTF-8"))(_ => response.body)
          Ok(responseBody).as("text/html")
        }
      }
    )
  }





}





