package controllers

import javax.inject.Inject

import models.{Owner}
import org.joda.time.DateTime
import play.api.libs.json.{Writes, JsValue, Json}
import play.api.mvc._
import services.OwnerDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OwnerController  @Inject() (ownerDAO: OwnerDAO)  extends Controller {

  def AddOwner() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    mapBody.map {
      data => {
        val owner_nickName = data("owner_nickName").mkString
        val password = data("password").mkString
        ownerDAO.exists(owner_nickName).flatMap  {
          case false  =>  Future.successful( Ok(" 该用户名，已经被占用咯，请使用别的用户名的吧！！！"))
          case true => {
            val newIdentity = { Owner(0,  owner_nickName,  "owner_realName", password,12345678999L,"email", "memo", true, new DateTime()) }
            println("new Owner is " + owner_nickName)
            ownerDAO.addOwner(newIdentity).map(res => Ok(" new Owner info added successfully!!!") )}}
      }
    }.getOrElse(Future.successful(Ok("Error8!!!")))
  }

   //  直接通过表单提交过来的数据，同数据库中查询得到的数据进行比对，正确则跳转到相应的页面且设定相应的session！
   def LoginOwner() =  Action.async {implicit request =>
     val body: AnyContent = request.body
     val mapBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
     mapBody.map {
             data => {
               val owner_nickName = data("owner_nickName").mkString
               val password = data("password").mkString

               ownerDAO.checkOwner(owner_nickName, password).flatMap  {
                 case None  =>  Future.successful( Ok("账号，或用户名错误！！！"))
                 case Some(_) => {
                   val json: JsValue = Json.obj(
                     "data" -> "null",
                     "message" -> "登陆成功")
                   Future.successful( Ok(json).
                     withSession(request.session + ("owner_nickName" -> owner_nickName) + ("roles" -> "测试的角色!")   )  )}}
             }
           }.getOrElse(Future.successful(Ok("Error8!!!")))
   }

  def LogoutOwner() =  Action.async {implicit request =>
    val json: JsValue = Json.obj(
      "data" -> "null",
      "message" -> "退出成功")
    Future.successful(Ok(json).withNewSession)
  }


  def getcurrentOwner() =   Action.async {implicit request =>
    // implicit val ownerFormat = Json.format[Owner]
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    ownerDAO.getcurrentOwner(session_owner_nickName).map(
      res => {
        if (res.length == 0) {
          val json: JsValue = Json.obj(
            "data" -> "null",
            "message" -> "获取成功")
          Ok(json)
        }
        else {
          implicit val writer = new Writes[(Int, String, String)] {
            def writes(t: (Int, String, String)): JsValue = {
              Json.obj( "ownerid" -> t._1,
                        "owner_nickName" -> t._2,
                        "owner_realName" -> t._3 )}}
          val jsonArrayOfRmds = Json.toJson(res)
          val json: JsValue = Json.obj(
            "data" -> jsonArrayOfRmds,
            "message" -> "获取成功")
          Ok(json)
        }
      })
  }


 }
