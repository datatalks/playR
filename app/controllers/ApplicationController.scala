package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.UserDAO
import models.User,models.UserFormData
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationController   @Inject() (userDAO: UserDAO) extends Controller {

  val UserForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobile" -> longNumber,
      "email" -> email
    )(UserFormData.apply)(UserFormData.unapply)
  )

  def index = Action.async { implicit request =>
    userDAO.listAllUsers map { users =>
      Ok(views.html.index(UserForm, users))
    }
  }

  def addUser() = Action.async { implicit request =>
    UserForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email)
        userDAO.addUser(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    userDAO.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}

