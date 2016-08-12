package controllers

import javax.inject.Inject

import models.{Identity,IdentityFormData}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import dao.IdentityDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdentityController  @Inject() (identityDAO: IdentityDAO)  extends Controller {

  val IdentityForm = Form(
    mapping(
      "identity" -> nonEmptyText,
      "password" -> nonEmptyText
    )(IdentityFormData.apply)(IdentityFormData.unapply) )

  def AddIdentity() = Action.async { implicit request =>
    IdentityForm.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok("ERROR 8!!!")),
      data => {
                      identityDAO.getIdentity(data.identity).flatMap  {
                      case Some(_)  =>  Future.successful( Ok(" 该用户名，已经被占用咯，请使用别的用户名的吧！！！"))
                      case None => {
                      val newIdentity = { Identity(0, data.identity, data.password, "memo", true, new DateTime()) }
                      println("identity is " + data.identity)
                      identityDAO.addIdentity(newIdentity).map(res => Ok(" new Identity info added successfully!!!") )}}
              })
  }

   //  直接通过表单提交过来的数据，同数据库中查询得到的数据进行比对，正确则跳转到相应的页面且设定相应的session！
   def LoginIdentity() =  Action.async {implicit request =>
     IdentityForm.bindFromRequest.fold(
       // if any error in submitted data
       errorForm => Future.successful(Ok("ERROR 8!!!")),
       data => {
                 println("identity is " + data.identity  + "XXXXXX" + "password is " + data.password)
                 identityDAO.checkIdentity(data.identity, data.password).flatMap  {
                 case None  =>  Future.successful( Ok("账号，或用户名错误！！！"))
                 case Some(_) => {
                   Future.successful( Ok("已经登录成功，且已经通过 session 设定了 subject present 权限").
                     withSession(request.session + ("identity" -> data.identity) + ("roles" -> "foo_bar")   )  )}}
               })
   }

  def LogoutIdentity() =  Action.async {implicit request =>
    Future.successful(  Ok("Bye").withNewSession )
  }





 }
