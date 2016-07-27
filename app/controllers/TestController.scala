package controllers

import model.{User, UserForm}
import play.api.mvc._
import service.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Logger


import play.api.libs.json._

class TestController extends Controller {

  def index = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      Ok(views.html.index(UserForm.form, users))
    }
  }

  def xiaofan1 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      Ok(users.toString())
    }
  }

  def xiaofan2 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      Ok(users.toString)
    }
  }

  def xiaofan3 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(_.email).toString )
    }
  }


  def xiaofan4 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email,  t,  x.email, "!!!!!")).toString )
    }
  }

  def xiaofan5 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email + t + x.email +  "!!!!!")).toString )
    }
  }

  def xiaofan6 = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email + t + x.email +  "中文中文中文")).mkString  )
    }
  }

  def json1 = Action {
    val nieces = Seq("Aleka", "Christina", "Emily", "Hannah", "Molly")
    Ok(Json.toJson(nieces))
  }

  def json2 = Action.async  { implicit request =>{

    implicit val userWrites = new Writes[User] {
      def writes(user: User) = Json.obj(
        "id" -> user.id,
        "firstname" -> user.firstName.toUpperCase,
        "lastName" -> user.lastName,
        "lastName" -> user.lastName,
        "email888" -> user.email)
    }

    UserService.listAllUsers map {  users =>
      Ok(Json.toJson(users))}
  }
  }

  def r1 = Action.async  { implicit request =>
    import scala.sys.process._
    "touch XXX.txt".!
    val r:String = "$a " + "papapap啪啪啪啪啪"
    println("r is : " + r)
    // MAC OS sed 命令使用 VS Linux sed 命令的使用存在相应的差异和区别：
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  r , "XXX.txt").!
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  "$a XX777X", "/Users/datatalks/Desktop/xiaofan.txt").!
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  r, "/Users/datatalks/Desktop/xiaofanR.txt").!


    "R CMD BATCH R.R".!
    println("R.R Script run successfully！！！")
    println("succeeded!!!")

    Future.successful(Ok("This is the Test!!!" + r))

  }


  def r2 = Action.async  { implicit request =>

    Logger.info("Application startup...")


    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")


//    import laika.api._
//    import laika.parse.markdown._
//    import laika.render._
//    import laika.parse._
//
//    import laika.api.Parse
//    import laika.api.Render
//    import laika._
//    import laika.factory._
//
//    Transform from Markdown to laika.render.HTML fromFile "MarkDown/preview1.md" toFile "MarkDown/preview1.html"

    // Future.successful(Ok("This is the Test for R script!!!"))
    // Future.successful(Redirect("http://stackoverflow.com/questions/10962694"))

       Future.successful(Redirect("http://localhost:88/preview1.html"))
  }



  def r3 = Action.async  {

    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")

    import play.api.libs.ws._
    import play.api.Play.current
    WS.url("http://localhost:88/preview1.html").get().map { response =>
      Ok(new String(response.body.getBytes("ISO-8859-1") , response.header(CONTENT_ENCODING).getOrElse("UTF-8"))).as(HTML)
    }


  }

  def r4 = Action.async  {

    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")

    import play.api.libs.ws._
    import play.api.Play.current
    WS.url("http://localhost:88/preview1.html").get().map { response =>

      def responseBody = response.header(CONTENT_TYPE).filter(_.toLowerCase.contains("charset")).fold(new String(response.body.getBytes("ISO-8859-1") , "UTF-8"))(_ => response.body)
      val result = responseBody.toString
      Ok(result).as("text/html")
    }


  }








  def addUser() = Action.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(0, data.firstName, data.lastName, data.mobile, data.email)
        UserService.addUser(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    UserService.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}

