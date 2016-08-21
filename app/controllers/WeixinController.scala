package controllers

import play.Logger
import play.api.mvc._

import scala.concurrent.Future

class WeixinController extends Controller {

  def checkToken(signature: String, timestamp: String, nonce: String, echostr: String) = Action.async { implicit request =>
    Logger.info("receive weixin server pamameter signature=" + signature + ",timestamp=" + timestamp + ",nonce=" + nonce + ",echostr=" + echostr)
    Future.successful(Ok(echostr))

  }

  def xmlrequest = Action { request =>
    request.body.asXml.map { xml =>
      println("the request from the weixin is :" + xml)
      (xml \\ "signature" headOption).map(_.text).map { signature =>
        Ok(signature)
      }.getOrElse {
        BadRequest("Missing parameter [signature]")
      }
    }.getOrElse {
      BadRequest("Expecting Xml data")
    }
  }

  def xmlreponse = Action.async(parse.xml) { implicit request =>
    println( "Forget the request header, the request body is: " + request.body)
    val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("X")
    val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("X")
    val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("X")
    val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("X")
    val Content = (request.body \\ "Content" headOption).map(_.text).getOrElse("X")
    println(weixin)
    println(openid)
    Future.successful(Ok(
      <xml>
        <ToUserName>{openid}</ToUserName>
        <FromUserName>{weixin}</FromUserName>
        <CreateTime>{CreateTime}</CreateTime>
        <MsgType>{MsgType}</MsgType>
        <Content>{Content}</Content>
      </xml>
    ))
  }

}










