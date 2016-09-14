package controllers

import javax.inject.Inject

import models.{Owner}
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import security.Cipher
import services.{JoinDAO, OwnerRoleDAO, OwnerDAO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class OwnerController  @Inject() (ownerDAO: OwnerDAO, ownerRoleDAO: OwnerRoleDAO, joinDAO:JoinDAO)  extends Controller {
  def AddOwner() = Action.async { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    println("jsonBody======" + jsonBody)
    jsonBody.map {
      data => {
        val owner_nickName = (data \ "owner_nickName").as[String]
        val owner_realName = (data \ "owner_realName").as[String]
        val password = (data \ "password").as[String]
        val mobile = (data \ "mobile").as[String]
        val email = (data \ "email").as[String]

        ownerDAO.exists(owner_nickName).flatMap  {
          case true  =>
            { val json: JsValue = Json.obj(
              "data" -> "null",
              "message" -> "用户名重复(错误信息)")
              Future.successful( Ok(json))}
          case false => {
            val newIdentity = { Owner(0,  owner_nickName, owner_realName, password, mobile,email, "memo", true, new DateTime()) }
            val json: JsValue = Json.obj(
              "data" ->  Json.obj("owner_nickName" -> owner_nickName),
              "message" -> "用户创建成功")
            ownerDAO.addOwner(newIdentity).map(res => Ok(json))}}
      }
    }.getOrElse(Future.successful(Ok("Error8!!!")))
  }

  def listowner() = Action.async { implicit request =>

    ownerDAO.listOwner map { data =>
    {implicit val writer = new Writes[(Int, String, String, String, String, String, Boolean, DateTime)] {
        def writes(t: (Int, String, String, String, String, String, Boolean, DateTime)): JsValue = {
          Json.obj("id" -> t._1,
            "owner_nickName" -> t._2,
            "owner_realName" -> t._3,
            "mobile" -> t._4,
            "email" -> t._5,
            "memo" -> t._6,
            "status" -> t._7,
            "time" -> t._8)}}
      val jsonArrayOfOwners = Json.toJson(data)
      val json: JsValue = Json.obj(
        "data" -> jsonArrayOfOwners,
        "message" -> "请求成功")
      Ok(json)}}
  }

   //  直接通过表单提交过来的数据，同数据库中查询得到的数据进行比对，正确则跳转到相应的页面且设定相应的session！
   def LoginOwner() =  Action.async {implicit request =>
     val body: AnyContent = request.body
     val jsonBody: Option[JsValue] = body.asJson
     println("XXXXXXX is "+ body.toString)

     jsonBody.map {
             data => {
               val owner_nickName = (data \ "owner_nickName").as[String]
               val password =  (data \ "password").as[String]
               ownerDAO.checkOwner(owner_nickName,  password).flatMap  {
                 case None  =>
                   val json1: JsValue = Json.obj(
                     "data" -> "null",
                     "message" -> "账号，或用户名错误！")
                   Future.successful( Ok(json1))
                 case Some(_) => {
                   val json2: JsValue = Json.obj(
                     "data" -> owner_nickName,
                     "message" -> "登陆成功")
                   Future.successful( Ok(json2).
                     withSession(request.session + ("owner_nickName" -> Cipher(owner_nickName).encryptWith("playR"))
                       + ("roles" -> Cipher("accessOK").encryptWith("playR"))   ))}}}
           }.getOrElse(Future.successful(Ok("Error8!!!")))
   }

  def LogoutOwner() =  Action.async {implicit request =>
    val json: JsValue = Json.obj(
      "data" -> "null",
      "message" -> "退出成功")
    Future.successful(Ok(json).withNewSession)
  }

  def getcurrentOwner() =   Action.async {implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    val result  = Await.result(joinDAO.join4(Cipher(session_owner_nickName).decryptWith("playR")), Duration.Inf)
    val output = result groupBy(data => (data._1, data._2, data._3)) map {
      case (k, v) => (k, v map {case (k1, k2, k3, v) => v} )}
    val finals = for(data <- output.toList) yield (data._1._1,data._1._2,data._1._3,data._2.reduceLeft(_+"&"+_))

    val json: JsValue = if (finals.length == 0) {Json.obj(
        "data" -> "null",
        "message" -> "获取成功")}
    else {Json.obj(
          "data" -> Json.obj("ownerid" -> JsNumber(finals(0)._1),
                          "owner_nickName" -> JsString(finals(0)._2),
                          "owner_realName" -> JsString(finals(0)._3),
                          "role" -> finals(0)._4.toString.split("&")    ),
          "message" -> "获取成功")
    }
    Future.successful(Ok(json))
  }

 }
