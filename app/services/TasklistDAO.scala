package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.Tasklist
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class TasklistDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val tasklist = TableQuery[TaskListTableDef]

  def exists(report_id : Int, scheduled_execution_time :DateTime) : Future[Boolean] =
    db.run(tasklist.filter(x => (x.report_id === report_id && x.scheduled_execution_time===scheduled_execution_time)).exists.result)

  def addTask(task : Tasklist): Future[String] = {
    db.run(tasklist += task).map(res => "Rmd successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }}

  def delete(taskid: Int): Future[Int] = {
    db.run(tasklist.filter(_.taskid === taskid).delete)
  }

  def getTask(taskid: Int): Future[Option[Tasklist]] = {
    db.run(tasklist.filter(_.taskid === taskid).result.headOption)
  }

  def upadte_start_time(taskid :Int , execution_start_time:DateTime) = {
    db.run(tasklist.filter(_.taskid === taskid).map(_.execution_start_time).update(execution_start_time))
  }

  def upadte_finish_time(taskid :Int , execution_finish_time:DateTime) = {
    db.run(tasklist.filter(_.taskid === taskid).map(_.execution_finish_time).update(execution_finish_time))
  }

  def listAll: Future[Seq[Tasklist]] = {
    db.run(tasklist.result)
  }



  class TaskListTableDef(tag: Tag) extends Table[Tasklist](tag, "tasklist") {

    def taskid = column[Int]("taskid", O.PrimaryKey,O.AutoInc)
    def report_id = column[Int]("report_id")
    def owner_nickName = column[String]("owner_nickName")
    def scheduled_execution_time = column[org.joda.time.DateTime]("scheduled_execution_time")
    def execution_start_time = column[org.joda.time.DateTime]("execution_start_time")
    def execution_finish_time = column[org.joda.time.DateTime]("execution_finish_time")
    def reportfileName = column[String]("reportfileName")

    override def * = (taskid,report_id,owner_nickName,
        scheduled_execution_time, execution_start_time, execution_finish_time,reportfileName) <> (Tasklist.tupled, Tasklist.unapply _)
  }
}
