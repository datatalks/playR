package controllers

import javax.inject.Inject
import models.{Report}
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}
import play.Logger
import play.api.libs.json._
import play.api.mvc._
import security.Cipher
import services.{JoinDAO, ReportDAO}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import env.env


class Report2Controller  @Inject() (reportDAO: ReportDAO, joinDAO: JoinDAO) extends Controller {
  def listReport(pageNo:Int, pageSize:Int) = Action.async { implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    reportDAO.getOwnerminiReport(Cipher(session_owner_nickName).decryptWith("playR"),pageNo -1, pageSize)._1.map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "请求成功")
          Ok(json)
        }
        else {
          val rows  = Await.result(reportDAO.getOwnerminiReport(Cipher(session_owner_nickName).decryptWith("playR"),pageNo -1, pageSize)._2, Duration.Inf)
          implicit val writer = new Writes[(Int, String, String, String, DateTime)] {
            def writes(t: (Int, String, String, String,DateTime)): JsValue = {
              Json.obj( "id" -> t._1,
                "owner" -> t._2,
                "reportName" -> t._3,
                "execute_type" -> t._4,
                "modify_time" -> t._5)}}
          val jsonArrays = Json.toJson(res)
          val json: JsValue = Json.obj(
            "data" -> jsonArrays,
            "page" -> Json.obj("currentPageNo" -> pageNo, "pageSize" -> pageSize, "totalCount" -> rows.toString, "totalPageCount" -> math.ceil(rows.toFloat/pageSize).toInt ),
            "message" -> "请求成功")
          Ok(json)}})}


  def listReport2(pageNo:Int, pageSize:Int) = Action.async { implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    joinDAO.reportList(Cipher(session_owner_nickName).decryptWith("playR"),pageNo -1, pageSize)._1.map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "请求成功")
          Ok(json)
        }
        else {
          val rows  = Await.result(joinDAO.reportList(Cipher(session_owner_nickName).decryptWith("playR"),pageNo -1, pageSize)._2, Duration.Inf)
          implicit val writer = new Writes[(Int, String, String, String,DateTime,DateTime,Int,DateTime, Option[Int] ,Option[DateTime])] {
            def writes(t: (Int, String, String, String,DateTime,DateTime,Int,DateTime, Option[Int] ,Option[DateTime])): JsValue = {
              Json.obj( "report_id" -> t._1,
                "owner_nickName" -> t._2,
                "reportName" -> t._3,
                "execute_type" -> t._4,
                "once_scheduled_execute_time" -> t._5,
                "circle_scheduled_start_time" -> t._6,
                "circle_scheduled_interval_minutes" -> t._7,
                "circle_scheduled_finish_time" -> t._8,
                "taskid" -> t._9,
                "report_last_execution_time" -> t._10
              )}}
          val jsonArrays = Json.toJson(res)
          val json: JsValue = Json.obj(
            "data" -> jsonArrays,
            "page" -> Json.obj("currentPageNo" -> pageNo, "pageSize" -> pageSize, "totalCount" -> rows.toString, "totalPageCount" -> math.ceil(rows.toFloat/pageSize).toInt ),
            "message" -> "请求成功")
          Ok(json)}})}


  def addReport() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    jsonBody.map {
      data => {
        val iniTime = DateTime.parse("01/01/1970 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))
        val owner_nickName = Cipher(session_owner_nickName).decryptWith("playR")
        val reportName = (data \ "reportName").as[String]
        val reportContent = (data \ "reportContent").as[String]
        val execute_type = (data \ "execute_type").as[String]
        val newReport = if( execute_type == "once")
                            Report(0, owner_nickName, reportName, reportContent, "once",
                              DateTime.parse((data \ "once_scheduled_execute_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
                              iniTime, 0, iniTime, new DateTime(), 1)
                        else
                            Report(0, owner_nickName, reportName, reportContent, "circle", iniTime,
                              DateTime.parse((data \ "circle_scheduled_start_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
                              (data \ "circle_scheduled_interval_minutes").as[Int],
                              DateTime.parse((data \ "circle_scheduled_finish_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
                              new DateTime(), 1)

        val json: JsValue = Json.obj(
          "data" -> "null",
          "message" -> "保存成功")

        reportDAO.addReport(newReport).map(res => Ok(json))
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }

  def updateReport(id : Int) = Action.async { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    jsonBody.map {
      data => {
        val iniTime = DateTime.parse("01/01/1970 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))
        val owner_nickName = Cipher(session_owner_nickName).decryptWith("playR")
        val reportName = (data \ "reportName").as[String]
        val reportContent = (data \ "reportContent").as[String]
        val execute_type = (data \ "execute_type").as[String]
        val newReport = if( execute_type == "once")
          Report(0, owner_nickName, reportName, reportContent, "once",
            DateTime.parse((data \ "once_scheduled_execute_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
            iniTime, 0, iniTime, new DateTime(), 1)
        else
          Report(0, owner_nickName, reportName, reportContent, "circle", iniTime,
            DateTime.parse((data \ "circle_scheduled_start_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
            (data \ "circle_scheduled_interval_minutes").as[Int],
            DateTime.parse((data \ "circle_scheduled_finish_time").as[String], DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC()),
            new DateTime(), 1)

        val json: JsValue = Json.obj(
          "data" -> "null",
          "message" -> "保存成功")

        reportDAO.updateReport(id ,newReport).map(res => Ok(json))
      }
    }.getOrElse(Future.successful(Ok("Error!!!")))
  }


  def getReport(id : Int) = Action.async { implicit request =>
          val rows  = Await.result(reportDAO.get(id), Duration.Inf)
          implicit val writer = new Writes[(Int, String, String, String, String, DateTime,DateTime,Int,DateTime,DateTime,Int)] {
            def writes(t: (Int, String, String, String, String, DateTime,DateTime,Int,DateTime,DateTime,Int)): JsValue = {
              Json.obj( "id" -> t._1,
                "owner_nickName" -> t._2,
                "reportName" -> t._3,
                "reportContent" -> t._4,
                "execute_type" -> t._5,
                "once_scheduled_execute_time" -> t._6,
                "circle_scheduled_start_time" -> t._7,
                "circle_scheduled_interval_minutes" -> t._8,
                "circle_scheduled_finish_time" -> t._9,
                "modify_time" -> t._10,
                "status" -> t._11)}}
          val json = Json.toJson(rows)
    Future.successful(Ok(json))
  }






  def reportRhtml(fileName: String) = Action.async { implicit request =>
    val htmlContent = scala.io.Source.fromFile(s"MarkDown/reportR/RMD/$fileName/$fileName.html").mkString
    Future.successful(Ok(htmlContent).as(HTML))
  }

//  因为将 reportURL 从 report 表中删除,故该方法则需要注销!
//  def report2Rhtml(reportUrl: String  ) = Action.async { implicit request =>
////    val owner_nickName = reportUrl.split("Report")(0)
////    val fileName = reportUrl.split("Report")(1)
//    val fileName = reportUrl
//    val ReportContent = Await.result(reportDAO.getreportContent(reportUrl), Duration.Inf)(0)
//    // 以下部分不论是前端提供,还是从数据库中获取都是同样的流程!!!
//    val path = "MarkDown/reportR/RMD/" + fileName
//    import scala.sys.process._
//    (s"mkdir -p -- $path ").!   //  Make directory if it doesn't exist!
//    scala.tools.nsc.io.File(path + "/" + fileName + ".Rmd").writeAll(ReportContent) // 删除了之前存在的内容!
//    val dir = env.dir
//    val Rfile_1delete_2append = dir + "/MarkDown/reportR/Rshell/" + fileName + ".R"
//    (s"rm -f $Rfile_1delete_2append").!
//    scala.io.Source.fromFile("reportR.R").getLines.
//      foreach { line => scala.tools.nsc.io.File("MarkDown/reportR/Rshell/" + fileName + ".R").
//        appendAll(line.replace("$fileR", fileName).replace("$dirR", dir) + sys.props("line.separator"))
//    }
//    import scala.sys.process._
//    (s"R CMD BATCH MarkDown/reportR/Rshell/$fileName.R").!
//    val htmlContent = scala.io.Source.fromFile(s"MarkDown/reportR/RMD/$fileName/$fileName.html").mkString
//    Logger.info(fileName + ".html has been responsed!!!")
//    Future.successful(Ok(htmlContent).as(HTML))}


  def getOwnerReport(owner : String) = Action.async { implicit request =>
    implicit val reportFormat = Json.format[Report]
    Logger.info("xinyang++++++" + Cipher("xinyang").encryptWith("playR"))
    Logger.info("xiaofan++++++" + Cipher("xiaofan").encryptWith("playR"))

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
          Ok(json)}})}


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
              Ok(json)}})}}.getOrElse(Future.successful(Ok("Error!!!")))}

}




