package controllers

import play.Logger
import play.api.mvc._
import scala.concurrent.Future
import services.XmlFeedback
import services.XmlVoice

import scala.runtime.ScalaRunTime._

class WeixinController extends Controller {

  implicit val myCustomCharset = Codec.javaSupported("iso-8859-1")

  def checkToken(signature: String, timestamp: String, nonce: String, echostr: String) = Action.async { implicit request =>
    Logger.info("receive weixin server pamameter signature=" + signature + ",timestamp=" + timestamp + ",nonce=" + nonce + ",echostr=" + echostr)
    Future.successful(Ok(echostr))
  }

    def xmlreponse = Action.async(parse.xml) { implicit request =>
//      println( "Forget the request header, the request body is: " + request.body)
//      val transcode = new String(request.body.toString.getBytes("ISO-8859-1") , "UTF-8")
//      print("UTF-8的信息如下:" + transcode)
      val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("X")
      val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("X")
      val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("X")
      val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("X")
      val Recognition = (request.body \\ "Recognition" headOption).map(_.text).getOrElse("X")
      val Recognition2UTF8 = new String(Recognition.getBytes("ISO-8859-1") , "UTF-8")

      MsgType match{
        case "text"   => { Future.successful(Ok(XmlFeedback.textFeedback(openid, weixin, CreateTime, "on the developing...")))}
        case "voice"  => {
          Logger.info( "voice source" +  Recognition)
          Logger.info( "voice UTF8" +  Recognition2UTF8)

          val input2pinyin = XmlVoice.chinese2pinyin(Recognition2UTF8)
          val result = XmlVoice.subject2similarity( input2pinyin )

          val temp = XmlVoice.subject2similarity(Recognition2UTF8)
          Logger.info(stringOf(temp))
          Logger.info("控制面板打印出来的结果是: " + temp)
          Future.successful(Ok(XmlFeedback.textFeedback(openid, weixin, CreateTime, result)))}
          //Future.successful(Ok(XmlFeedback.voiceFeedback(openid, weixin, CreateTime,Recognition) ))}
        case  _       => { Future.successful(Ok(XmlFeedback.othersFeedback(openid, weixin, CreateTime)))}
      }
  }

}










