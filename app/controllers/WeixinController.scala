package controllers

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


}










