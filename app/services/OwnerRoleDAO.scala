package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.OwnerRole
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class OwnerRoleDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val ownerRoles = TableQuery[OwnerRoleTableDef]


  def addOwnerRole(ownerRole: OwnerRole): Future[String] = {
      db.run(ownerRoles += ownerRole).map(res => "Identity successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteOwnerRole(id: Int): Future[Int] = {
      db.run(ownerRoles.filter(_.id === id).delete)
  }

  def getOwnerRole(owner_nickName: String): Future[Option[OwnerRole]] = {
      db.run(ownerRoles.filter(_.owner_nickName === owner_nickName).result.headOption)
  }

  def checkOwnerRole(owner_nickName: String, role:String): Future[Option[OwnerRole]] = {
      db.run(ownerRoles.filter(_.owner_nickName === owner_nickName).filter(_.role === role).result.headOption)
  }

  def listAllOwnerRole: Future[Seq[OwnerRole]] = {
      db.run(ownerRoles.result)
  }


  class OwnerRoleTableDef(tag: Tag) extends Table[OwnerRole](tag, "ownerRole") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def owner_nickName = column[String]("owner_nickName")
    def role = column[String]("role")
    def memo = column[String]("memo")
    def status = column[Boolean]("status")
    def time = column[org.joda.time.DateTime]("time")

    override def * =
      (id, owner_nickName, role, memo,status, time) <> (OwnerRole.tupled, OwnerRole.unapply _)
  }
}