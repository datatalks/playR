package dao

import scala.concurrent.Future
import javax.inject.Inject
import models.{Identity}

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import com.github.tototoshi.slick.MySQLJodaSupport._

class IdentityDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val identitys = TableQuery[IdentityTableDef]


  def addIdentity(identity: Identity): Future[String] = {
      db.run(identitys += identity).map(res => "Identity successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteIdentity(id: Int): Future[Int] = {
      db.run(identitys.filter(_.id === id).delete)
  }

  def getIdentity(identity: String): Future[Option[Identity]] = {
      db.run(identitys.filter(_.identity === identity).result.headOption)
  }

  def checkIdentity(identity: String, password:String): Future[Option[Identity]] = {
      db.run(identitys.filter(_.identity === identity).filter(_.password === password).result.headOption)
  }

  def listAllIdentity: Future[Seq[Identity]] = {
      db.run(identitys.result)
  }


  class IdentityTableDef(tag: Tag) extends Table[Identity](tag, "identity") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def password = column[String]("password")
    def identity = column[String]("identity")
    def memo = column[String]("memo")
    def status = column[Boolean]("status")
    def time = column[org.joda.time.DateTime]("time")

    override def * =
      (id, identity, password, memo, status, time) <> (Identity.tupled, Identity.unapply _)
  }
}