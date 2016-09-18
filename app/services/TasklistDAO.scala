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

  def addTask(task : Tasklist): Future[String] = {
    Logger.info(s"logTest: 您输入的 RMD 内容是  $task")
    db.run(tasklist += task).map(res => "Rmd successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }}

  def delete(taskid: Int): Future[Int] = {
    db.run(tasklist.filter(_.taskid === taskid).delete)
  }

  def getTask(taskid: Int): Future[Option[Tasklist]] = {
    db.run(tasklist.filter(_.taskid === taskid).result.headOption)
  }

  def listAll: Future[Seq[Tasklist]] = {
    db.run(tasklist.result)
  }

  class TaskListTableDef(tag: Tag) extends Table[Tasklist](tag, "tasklist") {

    def taskid = column[Int]("taskid", O.PrimaryKey,O.AutoInc)
    def report_id = column[Int]("report_id")
    def owner_nickName = column[String]("owner_nickName")
    def reportContent = column[String]("reportContent")
    def reportURL = column[String]("reportURL")
    def scheduled_time = column[org.joda.time.DateTime]("scheduled_time")
    def executed_start_time = column[org.joda.time.DateTime]("executed_start_time")
    def executed_finish_time = column[org.joda.time.DateTime]("executed_finish_time")

    override def * =
      (taskid,report_id,owner_nickName,reportContent,reportURL,
        scheduled_time, executed_start_time, executed_finish_time ) <> (Tasklist.tupled, Tasklist.unapply _)
  }
}
