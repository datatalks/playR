package controllers

import play.Logger
import play.api.mvc._

import scala.concurrent.Future
import services.XmlFeedback

class WeixinController extends Controller {

 implicit val myCustomCharset = Codec.javaSupported("iso-8859-1")
  def checkToken(signature: String, timestamp: String, nonce: String, echostr: String) = Action.async { implicit request =>
    Logger.info("receive weixin server pamameter signature=" + signature + ",timestamp=" + timestamp + ",nonce=" + nonce + ",echostr=" + echostr)
    Future.successful(Ok(echostr))
  }

    def xmlreponse = Action.async(parse.xml) { implicit request =>
      println( "Forget the request header, the request body is: " + request.body)
      val transcode = new String(request.body.toString.getBytes("ISO-8859-1") , "UTF-8")
      print("UTF-8的信息如下:" + transcode)
      val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("")
      val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("")
      val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("")
      val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("")
      val Recognition = (request.body \\ "Recognition" headOption).map(_.text).getOrElse("")

      MsgType match{
        case "text"   => { Future.successful(Ok(XmlFeedback.textFeedback(openid, weixin, CreateTime)))}
        case "voice"  => { Future.successful(Ok(XmlFeedback.voiceFeedback(openid, weixin, CreateTime,Recognition) ))}
        case  _       =>  {Future.successful(Ok(XmlFeedback.othersFeedback(openid, weixin, CreateTime)))}
      }
  }

}










