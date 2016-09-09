package services

import org.joda.time.DateTime

import scala.concurrent.Future
import javax.inject.Inject
import models.Owner
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import com.github.tototoshi.slick.MySQLJodaSupport._

class OwnerDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val owners = TableQuery[OwnerTableDef]


  def addOwner(owner: Owner): Future[String] = {
      db.run(owners += owner).map(res => "Identity successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteOwner(id: Int): Future[Int] = {
      db.run(owners.filter(_.id === id).delete)
  }

  def exists(owner_nickName : String) : Future[Boolean] =
    db.run(owners.filter(_.owner_nickName === owner_nickName).exists.result)

  def getcurrentOwner(owner_nickName: String): Future[Seq[(Int, String, String)]] = {
    val query = owners.filter(_.owner_nickName === owner_nickName).
      map(data => (data.id, data.owner_nickName, data.owner_realName))
    db.run(query.result)
  }

  def checkOwner(owner_nickName: String, password:String): Future[Option[Owner]] = {
      db.run(owners.filter(_.owner_nickName === owner_nickName).filter(_.password === password).result.headOption)
  }

  def listOwner: Future[Seq[(Int, String , String, Long, String, String, Boolean, DateTime)]] = {
    val query = owners.map(data => ( data.id,data.owner_nickName,data.owner_realName, data.mobile, data.email, data.memo, data.status, data.time))
    db.run(query.result)
  }

  def listAllOwner: Future[Seq[Owner]] = {
      db.run(owners.result)
  }

  class OwnerTableDef(tag: Tag) extends Table[Owner](tag, "owner") {
    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def owner_nickName = column[String]("owner_nickName")
    def owner_realName = column[String]("owner_realName")
    def password = column[String]("password")
    def mobile = column[Long]("mobile")
    def email = column[String]("email")
    def memo = column[String]("memo")
    def status = column[Boolean]("status")
    def time = column[org.joda.time.DateTime]("time")

    override def * =
      (id, owner_nickName,owner_realName, password,mobile,email,memo, status, time) <> (Owner.tupled, Owner.unapply _)
  }
}
