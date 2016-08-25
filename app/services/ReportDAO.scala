package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Report}
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class ReportDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val reports = TableQuery[ReportTableDef]


  def addRmd(report : Report): Future[String] = {
    Logger.info(s"logTest: 您输入的 RMD 内容是  $report")
    db.run(reports += report).map(res => "Rmd successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    db.run(reports.filter(_.id === id).delete)
  }

  def get(id: Int): Future[Option[Report]] = {
    db.run(reports.filter(_.id === id).result.headOption)
  }

  def getOwnerRmd(owner: String): Future[Seq[Report]] = {
    val query = reports.filter(_.owner === owner)
      db.run(query.result) }


  def getOwnerRmds(owner: String): Future[Option[Report]] = {
    val query = reports.filter(_.owner === owner)
    db.run(query.result.headOption)}


  def listAll: Future[Seq[Report]] = {
    db.run(reports.result)
  }


  class ReportTableDef(tag: Tag) extends Table[Report](tag, "report") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def owner = column[String]("owner")
    def reportName = column[String]("reportName")
    def reportContent = column[String]("reportContent")
    def execute_type = column[String]("execute_type")
    def forward_execute_time = column[org.joda.time.DateTime]("forward_execute_time")
    def circle_execute_interval_seconds = column[Int]("circle_execute_interval_seconds")
    def modify_time = column[org.joda.time.DateTime]("modify_time")
    def reportUrl = column[String]("reportUrl")

    override def * =
      (id, owner, reportName,reportContent, execute_type, forward_execute_time,circle_execute_interval_seconds,modify_time,reportUrl) <> (Report.tupled, Report.unapply _)
  }
}