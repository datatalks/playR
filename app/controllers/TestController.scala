package controllers

import javax.inject.Inject
import com.github.stuxuhai.jpinyin.{PinyinFormat, PinyinHelper}
import models.{UserFormData, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.{JoinDAO, UserDAO}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import play.api.Logger
import play.api.libs.json._
import scala.util.Try


class TestController   @Inject() (userDAO: UserDAO,  joinDAO: JoinDAO, ws:WSClient) extends Controller {

  def pinyin()= Action.async { implicit request =>

    val res1 = PinyinHelper.convertToPinyinString("李.成. 竹。。。 ", ",", PinyinFormat.WITHOUT_TONE)
    val res2 = res1.replaceAll(",",  "")
    val res3 = res1.split(",")
    Future.successful(  Ok( res1 )  )
  }




  def pinyinn()= Action.async { implicit request =>
    val res1 = PinyinHelper.convertToPinyinString("李.成. 竹。。。 ", "$", PinyinFormat.WITHOUT_TONE)
    val res2 = res1.replaceAll(",",  "")
    val res3 = res1.split(",")
    Future.successful(  Ok( res1 )  )
  }

  def getsessionvalue() = Action.async { implicit request =>
    val session = request.session.get("owner_nickName").mkString
    println("session ===" + session)
    Future.successful(  Ok("session ===" + session )  )
  }



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


  def innerJoin1 =  Action.async { implicit request =>

    val res = joinDAO.join1
    for( i <- res) println("XXXXX  is" + i)
    Future.successful(Ok(res.toString))
  }

  def innerJoin2 =  Action.async { implicit request =>
    val res = joinDAO.join2
    for( i <- res) println(i)

    println("the res is " + res.toString)

    val result: Try[Seq[(String, String)]] = Await.ready(res, Duration.Inf).value.get
    Future.successful(Ok(result.toString))
  }


  //  对于表关联中使用异步Future得到的结果，通过两个map实现了异步结果的HTTP response!
  //  备注，对于常见的 Future 类型的结果返回错误如下：
  // Cannot write an instance of Seq[(String, String)] to HTTP response. Try to define a Writeable[Seq[(String, String)]]
  def innerJoin3 =  Action.async { implicit request =>
      joinDAO.join3 map { res =>
        val t = ";"
        Ok(   res.map( x => x._1 + t + x._2 ).mkString   )
      }
  }


  def xiaofan1 = Action.async { implicit request =>
    userDAO.listAllUsersforTestig map { users =>
      Ok(users.mkString)
    }
  }

  def xiaofan2 = Action.async { implicit request =>
    userDAO.listAllUsersforTestig map { users =>
      Ok(users.toString)
    }
  }

  def xiaofan3 = Action.async { implicit request =>
    userDAO.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(_.email).toString)
    }
  }


  def xiaofan4 = Action.async { implicit request =>
    userDAO.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email, t, x.email, "!!!!!")).toString)
    }
  }

  def xiaofan5 = Action.async { implicit request =>
    userDAO.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email + t + x.email + "!!!!!")).toString)
    }
  }

  def xiaofan6 = Action.async { implicit request =>
    userDAO.listAllUsers map { users =>
      val t = ";"
      Ok(users.map(x => (x.email + t + x.email + "中文中文中文")).mkString)
    }
  }

  def json1 = Action {
    val nieces = Seq("Aleka", "Christina", "Emily", "Hannah", "Molly")
    Ok(Json.toJson(nieces))
  }

  def json2 = Action.async { implicit request => {

    implicit val userWrites = new Writes[User] {
      def writes(user: User) = Json.obj(
        "id" -> user.id,
        "firstname" -> user.firstName.toUpperCase,
        "lastName" -> user.lastName,
        "lastName" -> user.lastName,
        "email888" -> user.email)
    }
    userDAO.listAllUsers map { users =>
      Ok(Json.toJson(users))
    }}}

  def json3 = Action {
    val name = "我是李爬爬"
    val json1: JsValue = Json.obj(
      "name" -> name,
      "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
      "residents" -> Json.arr(
        Json.obj(
          "name" -> "Fiver",
          "age" -> 4,
          "role" -> JsNull
        ),
        Json.obj(
          "name" -> "Bigwig",
          "age" -> 6,
          "role" -> "Owsla"
        )
      )
    )
    val url = "http://XXXX"
    val json2: JsValue = Json.obj(
      "data" -> url,
      "message" -> "提交成功咯~~~"
    )

    val json3: JsValue = Json.obj(
      "data" -> Json.arr(
        Json.obj(
          "title" -> "报告名称",
          "date" -> "生成报告日期",
          "url" -> "生成报告地址"
        )
      ),
      "message" -> "提交成功咯~~~"
    )


    Ok(json3)
  }



  def r1 = Action.async { implicit request =>
    import scala.sys.process._
    "touch XXX.txt".!
    val r: String = "$a " + "papapap啪啪啪啪啪"
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


  def r2 = Action.async { implicit request =>

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


  def r3 = Action.async {

    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")


    ws.url("http://localhost:88/preview1.html").get().map { response =>
      Ok(new String(response.body.getBytes("ISO-8859-1"), response.header(CONTENT_ENCODING).getOrElse("UTF-8"))).as(HTML)
    }


  }

  def r4 = Action.async {

    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")

    ws.url("http://localhost:88/preview1.html").get().map { response =>

      def responseBody = response.header(CONTENT_TYPE).filter(_.toLowerCase.contains("charset")).fold(new String(response.body.getBytes("ISO-8859-1"), "UTF-8"))(_ => response.body)
      val result = responseBody.toString
      Ok(result).as("text/html")
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

  def setsessions () = Action.async { implicit request =>
    Future.successful(  Ok(" sessions are setted or updated!").withSession(
      request.session + ("identity" -> "YYY8888BBBBBBBBBBBYYY"))  )
  }


  def getsessions () = Action.async { implicit request =>
    Future.successful(  request.session.get("identity").map { content =>
      Ok("Hello " + content)
    }.getOrElse {Unauthorized("Oops, you are not connected")}  )
  }

  def rmsessions () = Action.async { implicit request =>
    Future.successful(  Ok("Bye").withNewSession )
  }



}










