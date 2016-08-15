package dao

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.Rmd
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class RmdDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val rmds = TableQuery[RmdTableDef]


  def addRmd(rmd: Rmd): Future[String] = {
    Logger.info(s"logTest: 您输入的 RMD 内容是  $rmd")
    db.run(rmds += rmd).map(res => "Rmd successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    db.run(rmds.filter(_.id === id).delete)
  }

  def get(id: Int): Future[Option[Rmd]] = {
    db.run(rmds.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Rmd]] = {
    db.run(rmds.result)
  }


  class RmdTableDef(tag: Tag) extends Table[Rmd](tag, "rmd") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def owner = column[String]("owner")
    def reportR = column[String]("reportR")
    def execute_type = column[String]("execute_type")
    def forward_execute_time = column[org.joda.time.DateTime]("forward_execute_time")
    def circle_execute_interval_seconds = column[Int]("circle_execute_interval_seconds")
    def modify_time = column[org.joda.time.DateTime]("modify_time")

    override def * =
      (id, owner, reportR, execute_type, forward_execute_time,circle_execute_interval_seconds,modify_time) <> (Rmd.tupled, Rmd.unapply _)
  }
}