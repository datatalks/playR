package controllers

import javax.inject.Inject
import models.{Rmd, RmdFormData}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import dao.RmdDAO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ReportController   @Inject() (rmdDAO: RmdDAO) extends Controller {


  val  RmdForm  = Form(
      mapping(
        "owner" -> nonEmptyText,
        "reportR" -> nonEmptyText
      )(RmdFormData.apply)(RmdFormData.unapply))

  def addRmd() = Action.async { implicit request =>
    RmdForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("ERROR 8!!!")),
      data => {
        val newRmd = {Rmd(0, data.owner, data.reportR, "execute_type", new DateTime(), 123, new DateTime())}
        rmdDAO.addRmd(newRmd).map(res =>
          Ok(" new Rmd info added successfully!!!")
        )
      })
  }

}

