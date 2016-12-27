package controllers

import javax.inject.Inject
import play.api.libs.mailer._
import play.api.libs.ws.WSClient
import play.api.mvc._
import services._
import scala.concurrent.{Await, Future}
import yuyin.TTSDemo

class YuyinController   @Inject() (mailerClient: MailerClient, tasklistDAO:TasklistDAO, reportDAO:ReportDAO,  joinDAO: JoinDAO, ownerRoleDAO: OwnerRoleDAO,ws:WSClient) extends Controller {
  def yunyin = Action.async {
    val yunyin = new TTSDemo
    yunyin.startTTS("voice/xiaofan", "我叫李小凡")

    Future.successful(Ok("XXXXXX"))
  }

}










