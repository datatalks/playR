package controllers

import javax.inject.Inject

import models.{Report}
import org.joda.time.DateTime
import play.Logger
import play.api.libs.json._
import play.api.mvc._
import services.ReportDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Report2Controller   @Inject() (reportDAO: ReportDAO) extends Controller {

  def addReport() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    jsonBody.map {
      data => {
        val owner_nickName = session_owner_nickName
        val reportName = (data \ "reportName").as[String]
        val reportContent = (data \ "reportContent").as[String]

        val newReport = Report(0, owner_nickName, reportName, reportContent, "execute_type",
          new DateTime(), 123  , new DateTime(), new DateTime(), new DateTime(), "reportUrl",
          scala.util.Random.alphanumeric.take(10).mkString,1)

        val json: JsValue = Json.obj(
          "data" -> "null",
          "message" -> "保存成功")
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


  def listReport(pageNo:Int, pageSize:Int) = Action.async { implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    reportDAO.getOwnerminiReport(session_owner_nickName,pageNo -1, pageSize).map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "请求成功")
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
            "message" -> "请求成功")
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




