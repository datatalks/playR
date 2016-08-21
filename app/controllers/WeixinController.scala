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
    Logger.info("request charset is -[" + (if (request.charset == None) request.charset else request.charset.get) + "]")
    val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("")
    val Content = (request.body \\ "Content" headOption).map(_.text).getOrElse("")
    val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("")
    val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("")
    val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("")

    println(weixin)
    println(openid)
    println(MsgType)
    println(Content)
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

  def xmlreponse2 = Action.async(parse.xml) { implicit request =>
    println( "Forget the request header, the request body is: " + request.body)
    Logger.info("request charset is -[" + (if (request.charset == None) request.charset else request.charset.get) + "]")
    val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("")
    val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("")
    val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("")
    val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("X")
    val recongnition = (request.body \\ "Recongnition" headOption).map(_.text).getOrElse("")

    println(MsgType)
    MsgType match{
      case "text" => {  val result1 =       <xml>
                                                  <ToUserName>{openid}</ToUserName>
                                                  <FromUserName>{weixin}</FromUserName>
                                                  <CreateTime>{CreateTime}</CreateTime>
                                                  <MsgType>text</MsgType>
                                                  <Content><![CDATA[this is a text!!! for testing]]></Content>
                                                </xml>
        Future.successful(Ok(result1))}
      case "voice" =>  {  val result2 = <xml>
                                    <ToUserName>{openid}</ToUserName>
                                    <FromUserName>{weixin}</FromUserName>
                                    <CreateTime>{CreateTime}</CreateTime>
                                    <MsgType>text</MsgType>
                                    <Content>recongnition</Content>
                                  </xml>
        Future.successful(Ok(result2))}

      case _           =>  { val result3 =
                                              <xml>
                                                <ToUserName>{openid}</ToUserName>
                                                <FromUserName>{weixin}</FromUserName>
                                                <CreateTime>{CreateTime}</CreateTime>
                                                <MsgType>text</MsgType>
                                                <Content><![CDATA[Others MSGypes!!!]]></Content>
                                              </xml>
        Future.successful(Ok(result3))}

    }
  }
    def xmlreponse3 = Action.async(parse.xml) { implicit request =>
      println( "Forget the request header, the request body is: " + request.body)
      Logger.info("request charset is -[" + (if (request.charset == None) request.charset else request.charset.get) + "]")
      val weixin = (request.body \\ "ToUserName" headOption).map(_.text).getOrElse("")
      val openid = (request.body \\ "FromUserName" headOption).map(_.text).getOrElse("")
      val CreateTime = (request.body \\ "CreateTime" headOption).map(_.text).getOrElse("")
      val MsgType = (request.body \\ "MsgType" headOption).map(_.text).getOrElse("")
      val Recognition = (request.body \\ "Recognition" headOption).map(_.text).getOrElse("")
      println(Recognition)
      println(MsgType)


      MsgType match{
        case "text" => {  Future.successful(Ok( <xml>
                                          <ToUserName>{openid}</ToUserName>
                                          <FromUserName>{weixin}</FromUserName>
                                          <CreateTime>{CreateTime}</CreateTime>
                                          <MsgType>text</MsgType>
                                          <Content><![CDATA[this is a text!!! for testing]]></Content>
                                        </xml>))}
        case "voice" =>  {  Future.successful(Ok( <xml>
          <ToUserName>{openid}</ToUserName>
          <FromUserName>{weixin}</FromUserName>
          <CreateTime>{CreateTime}</CreateTime>
          <MsgType>text</MsgType>
          <Content>{Recognition}</Content>
        </xml>))}

        case _           =>  {Future.successful(Ok(<xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{CreateTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content><![CDATA[Others MSGypes!!!]]></Content>
          </xml>))}

      }
  }



}










