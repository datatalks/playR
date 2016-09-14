package controllers


import javax.inject.Inject
import models.{ReportFormData, Report}
import org.joda.time.DateTime
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.ReportDAO
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import scala.concurrent.Future


class ReportController   @Inject() (reportDAO: ReportDAO) extends Controller {
  val RmdForm = Form(
    mapping(
      "owner_nickName" -> nonEmptyText,
      "reportName" -> nonEmptyText,
      "reportContent" -> nonEmptyText
    )(ReportFormData.apply)(ReportFormData.unapply))


  def addReport() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
      data => {
        val owner_nickName = data("owner_nickName").mkString
        val reportName = data("reportName").mkString
        val reportContent = data("reportContent").mkString

        val newReport = Report(0, owner_nickName, reportName, reportContent, "execute_type",
          new DateTime(), 123  , new DateTime(), new DateTime(), new DateTime(), "reportUrl",1)

        case class JasonResult(data: String, message: String)
        implicit val JasonResultWrites = new Writes[JasonResult] {
          def writes(jasonResult: JasonResult) = Json.obj(
            "data" -> jasonResult.data,
            "message" -> jasonResult.message
          )
        }
        val res = JasonResult(reportName, "保存成功!")
        val json = Json.toJson(res)
        reportDAO.addReport(newReport).map(res => Ok(json))

      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }


  def reportRhtml(fileName: String) = Action.async { implicit request =>

    println("request=====" + request.toString )
    println("request.headers======" + request.headers.toString)
    println("request.body=======" + request.body.toString )
    val htmlContent = scala.io.Source.fromFile(s"MarkDown/reportR/RMD/$fileName/$fileName.html").mkString
    Logger.info(fileName + ".html has been responsed!!!")
    Future.successful(Ok(htmlContent).as(HTML))
  }



  def getOwnerReport(owner : String) = Action.async { implicit request =>
    implicit val reportFormat = Json.format[Report]
    reportDAO.getOwnerReport(owner).map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "XXXXXX")
          Ok(json)
        }
        else {
          val jsonArrayOfRmds = Json.toJson(res)
          val json: JsValue = Json.obj(
            "data" -> jsonArrayOfRmds,
            "message" -> "XXXXXX")
          Ok(json)
        }
      })
  }


  def getOwnerminiReport(owner : String) = Action.async { implicit request =>
    implicit val rmdFormat = Json.format[Report]
    reportDAO.getOwnerReport(owner).map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "XXXXXX")
          Ok(json)
        }
        else {
          implicit val writer = new Writes[(Int, String, String, String, DateTime, DateTime, String)] {
            def writes(t: (Int, String, String, String,DateTime, DateTime, String)): JsValue = {
              Json.obj( "id" -> t._1,
                        "owner" -> t._2,
                        "reportName" -> t._3,
                        "execute_type" -> t._4,
                        "once2circle_last_executed_time" -> t._5,
                        "modify_time" -> t._6,
                        "reportUrl" -> t._7)}}
          val jsonArrayOfRmds = Json.toJson(res)
          val json: JsValue = Json.obj(
            "data" -> jsonArrayOfRmds,
            "message" -> "XXXXXX")
          Ok(json)
        }
      })
  }



  def listOwnerReport() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
      data => {
        val owner = data("owner").mkString
        reportDAO.getOwnerReport(owner).map(
          res => {
            val temp = res.toList
            if (temp.length == 0) {
              val json: JsValue = Json.obj(
                "data" -> "null",
                "message" -> "XXXXXX")
              Ok(json)
            }
            else {
              implicit val reportFormat = Json.format[Report]
              val jsonArrayOfReports = Json.toJson(temp)
              val json: JsValue = Json.obj(
                "data" -> jsonArrayOfReports,
                "message" -> "XXXXXX")
              Ok(json)
            }
          })
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }

}




