package controllers

import play.Logger
import play.api.mvc._


class WeixinController extends Controller {

  def signature = Action { request =>
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


  def checkToken(signature: String, timestamp: String, nonce: String, echostr: String) = Action {
    Logger.info("receive weixin server pamameter signature=" + signature + ",timestamp=" + timestamp + ",nonce=" + nonce + ",echostr=" + echostr)
      Ok(echostr)
  }

}










