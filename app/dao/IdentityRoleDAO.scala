package dao

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{IdentityRole, Identity}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class IdentityRoleDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val identityRoles = TableQuery[IdentityRoleTableDef]


  def addIdentityRole(identityRole: IdentityRole): Future[String] = {
      db.run(identityRoles += identityRole).map(res => "Identity successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteIdentityRole(id: Int): Future[Int] = {
      db.run(identityRoles.filter(_.id === id).delete)
  }

  def getIdentityRole(identity: String): Future[Option[IdentityRole]] = {
      db.run(identityRoles.filter(_.identity === identity).result.headOption)
  }

  def checkIdentityRole(identity: String, role:String): Future[Option[IdentityRole]] = {
      db.run(identityRoles.filter(_.identity === identity).filter(_.role === role).result.headOption)
  }

  def listAllIdentityRole: Future[Seq[IdentityRole]] = {
      db.run(identityRoles.result)
  }


  class IdentityRoleTableDef(tag: Tag) extends Table[IdentityRole](tag, "identity") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def identity = column[String]("identity")
    def role = column[String]("role")
    def memo = column[String]("memo")
    def time = column[org.joda.time.DateTime]("time")

    override def * =
      (id, identity, role, memo, time) <> (IdentityRole.tupled, IdentityRole.unapply _)
  }
}