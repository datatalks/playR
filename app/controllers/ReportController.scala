package controllers


import javax.inject.Inject
import models.{ReportFormData, Report}
import org.joda.time.DateTime
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
    RmdForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("ERROR 8!!!")),
      data => {
        val newReport = Report(0, data.owner_nickName, data.reportName, data.reportContent, "execute_type", new DateTime(), 123  , new DateTime(), new DateTime(), new DateTime(), "reportUrl","RANDOM",1)
        case class JasonResult(data: String, message: String)
        implicit val JasonResultWrites = new Writes[JasonResult] {
          def writes(jasonResult: JasonResult) = Json.obj(
            "data" -> jasonResult.data,
            "message" -> jasonResult.message
          )
        }
        val res = JasonResult(data.reportName, "保存成功!")
        val json = Json.toJson(res)
        reportDAO.addReport(newReport).map(res => Ok(json))
      })
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
    reportDAO.getOwnerminiReport(owner).map(
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
    implicit val rmdFormat = Json.format[Report]
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
              val jsonArrayOfRmds = Json.toJson(temp)
              val json: JsValue = Json.obj(
                "data" -> jsonArrayOfRmds,
                "message" -> "XXXXXX")
              Ok(json)
            }
          })
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }

}




