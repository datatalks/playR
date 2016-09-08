package security

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler}
import play.api.mvc.{Request, Result, Results}
import models.Identity
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import models.SecurityRole

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    // e.g. request.session.get("user")
    val sessionidentity  = request.session.get("owner_nickName")
    val sessionroles  = request.session.get("roles").map(data => Cipher(data).decryptWith("playR")).mkString.split("_").map(x =>SecurityRole(x)).toList
    println("XXXXXXXXXXXXXXX: " + sessionidentity.mkString)
    println("XXXXXXXXXXXXXXX: " + sessionroles)
    sessionidentity match {
      case Some(_) =>  Future(Some(new Identity(sessionidentity.mkString, sessionroles) ))
      case None => Future(None)
    }
  }


  def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future {Results.Forbidden(views.html.accessFailed())}
  }
}
