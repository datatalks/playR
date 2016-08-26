package controllers

import play.Logger
import play.api.mvc._
import services.{XmlFeedback, XmlVoiceInput2}
import scala.concurrent.Future
import scala.runtime.ScalaRunTime._

class WeixinController2 extends Controller {

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
//          Logger.info( "voice source" +  Recognition)
//          Logger.info( "voice UTF8" +  Recognition2UTF8)
          // 根据服务器和本地的区别,针对性的选择 Recognition(本地),和Recognition2UTF8(服务器)两个版本的变量
          val input2pinyin = XmlVoiceInput2.chinese2pinyin(Recognition2UTF8)
          Logger.info("input2pinyin =========: " + input2pinyin)
          val result = XmlVoiceInput2.subject2similarity( input2pinyin )

//          val temp = XmlVoiceInput2.subject2similarity(Recognition)
//          Logger.info( "  "  +   stringOf(temp))
          Logger.info("result ========== " + result)
          Future.successful(Ok( XmlFeedback.newsFeedback(openid, weixin, CreateTime, result._3 , result._4 , result._5 , result._6  ) ))}
          //Future.successful(Ok(XmlFeedback.voiceFeedback(openid, weixin, CreateTime,Recognition) ))}
        case  _       => { Future.successful(Ok(XmlFeedback.othersFeedback(openid, weixin, CreateTime)))}
      }
  }

}










