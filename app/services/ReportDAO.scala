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
    db.run(reports += report).map(res => "Rmd successfully added").recover {
      case ex: Exception => ex.getCause.getMessage}
  }

  def delete(id: Int): Future[Int] = {
    db.run(reports.filter(_.id === id).delete)
  }

  def get(id: Int): Future[Option[Report]] = {
    db.run(reports.filter(_.id === id).result.headOption)
  }

//  因为将 reportURL 从 report 表中删除,故该方法则需要注销!
//  def getreportContent(reportUrl: String): Future[Seq[String]] = {
//    db.run(reports.filter(_.reportUrl === reportUrl).map(data => data.reportContent).result)
//  }

  def getOwnerReport (owner: String): Future[Seq[Report]] = {
    val query = reports.filter(_.owner_nickName === owner)
    db.run(query.result)}

  def getOwnerminiReport(owner: String, pageNo:Int, pageSize:Int): (Future[Seq[(Int, String, String, String, DateTime)]],Future[Int]) = {
    val query = reports.filter(_.owner_nickName === owner).sortBy(data => (data.id.asc)).
      map(data =>  (data.id ,data.owner_nickName, data.reportName, data.execute_type, data.modify_time )).
      drop(pageNo * pageSize).take(pageSize)
    val result =  db.run(query.result)
    val count = db.run(reports.filter(_.owner_nickName === owner).length.result)
    (result,count)
  }

  def scheduleReport(): (Future[Seq[(Int, String, String, String, DateTime,DateTime,Int,DateTime)]]) = {
    val query = reports.
      map(data =>  (data.id ,data.owner_nickName, data.reportName, data.execute_type,
        data.once_scheduled_execute_time,
        data.circle_scheduled_start_time, data.circle_scheduled_interval_minutes, data.circle_scheduled_finish_time))
    db.run(query.result)
  }

  def updateReport(id :Int, report: Report ): Future[Option[Report]] = {
    val query = reports.filter(_.id === id).update(report).map {
      case 0 => None
      case _ => Some(report)
    }
    db.run(query)
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
    def circle_scheduled_start_time = column[org.joda.time.DateTime]("circle_scheduled_start_time")
    def circle_scheduled_interval_minutes = column[Int]("circle_scheduled_interval_minutes")
    def circle_scheduled_finish_time = column[org.joda.time.DateTime]("circle_scheduled_finish_time")
    def modify_time = column[org.joda.time.DateTime]("modify_time")
    def status = column[Int]("status")

    override def * =
      (id, owner_nickName, reportName,reportContent, execute_type, once_scheduled_execute_time,
        circle_scheduled_start_time,  circle_scheduled_interval_minutes,circle_scheduled_finish_time,
        modify_time, status) <> (Report.tupled, Report.unapply _)
  }
}
