package controllers


import javax.inject.Inject
import models.{Rmd, RmdFormData}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.RmdDAO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._

import scala.concurrent.Future


class ReportController   @Inject() (rmdDAO: RmdDAO) extends Controller {
  val RmdForm = Form(
    mapping(
      "owner" -> nonEmptyText,
      "reportR" -> nonEmptyText
    )(RmdFormData.apply)(RmdFormData.unapply))

  def addRmd() = Action.async { implicit request =>
    RmdForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("ERROR 8!!!")),
      data => {
        val newRmd = Rmd(0, data.owner, data.reportR, "execute_type", new DateTime(), 123, new DateTime(), "www.playR")
        case class JasonResult(data: String, message: String)
        implicit val JasonResultWrites = new Writes[JasonResult] {
          def writes(jasonResult: JasonResult) = Json.obj(
            "data" -> jasonResult.data,
            "message" -> jasonResult.message
          )
        }
        val res = JasonResult(data.reportR, "保存成功!")
        val json = Json.toJson(res)
        rmdDAO.addRmd(newRmd).map(res => Ok(json))
      })
  }

  def getOwnerRmd(owner : String) = Action.async { implicit request =>
    implicit val rmdFormat = Json.format[Rmd]
    rmdDAO.getOwnerRmds(owner).map(
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

  def listOwnerRmd() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    implicit val rmdFormat = Json.format[Rmd]
    mapBody.map {
      data => {
        val owner = data("owner").mkString
        rmdDAO.getOwnerRmds(owner).map(
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




