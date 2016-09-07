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

  def addReport(report : Report): Future[String] = {
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

  def getOwnerReport (owner: String): Future[Seq[Report]] = {
    val query = reports.filter(_.owner_nickName === owner)
    db.run(query.result)}


  def getOwnerminiReport(owner: String, pageNo:Int, pageSize:Int): (Future[Seq[(Int, String, String, String,DateTime, DateTime, String)]],Future[Int]) = {
    val query = reports.filter(_.owner_nickName === owner).sortBy(data => (data.id.asc)).
      map(data =>  (data.id ,data.owner_nickName, data.reportName, data.execute_type, data.once2circle_last_executed_time, data.modify_time, data.reportUrl )).
      drop(pageNo * pageSize).take(pageSize)
    val result =  db.run(query.result)
    val count = db.run(reports.filter(_.owner_nickName === owner).length.result)
    (result,count)
  }


  def listAll: Future[Seq[Report]] = {
    db.run(reports.result)
  }


  class ReportTableDef(tag: Tag) extends Table[Report](tag, "report") {

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def owner_nickName = column[String]("owner_nickName")
    def reportName = column[String]("reportName")
    def reportContent = column[String]("reportContent")

    def execute_type = column[String]("execute_type")

    def once_scheduled_execute_time = column[org.joda.time.DateTime]("once_scheduled_execute_time")
    def circle_execute_interval_seconds = column[Int]("circle_execute_interval_seconds")
    def circle_next_scheduled_execute_time = column[org.joda.time.DateTime]("circle_next_scheduled_execute_time")

    def once2circle_last_executed_time = column[org.joda.time.DateTime]("once2circle_last_executed_time")
    def modify_time = column[org.joda.time.DateTime]("modify_time")
    def reportUrl = column[String]("reportUrl")
    def random = column[String]("random")
    def status = column[Int]("status")

    override def * =
      (id, owner_nickName, reportName,reportContent, execute_type, once_scheduled_execute_time,
        circle_execute_interval_seconds, circle_next_scheduled_execute_time,
        once2circle_last_executed_time,
        modify_time,reportUrl, random, status) <> (Report.tupled, Report.unapply _)
  }
}
