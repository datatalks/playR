package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Report, Owner}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class JoinDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, tasklistDAO:TasklistDAO,ownerDAO:OwnerDAO, reportDAO:ReportDAO, ownerRoleDAO:OwnerRoleDAO) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  def join1 : Future[Seq[(Owner, Report)]] = {
    val joinQuery1 = ownerDAO.owners.join(reportDAO.reports).on(_.owner_nickName === _.owner_nickName)
    db.run(joinQuery1.result)
  }

  def join2 : Future[Seq[(String, String)]] = {
    val joinQuery2 = for {(a,b) <- ownerDAO.owners join  reportDAO.reports on (_.owner_nickName === _.owner_nickName)}
                     yield (a.owner_nickName, b.reportName)
    db.run(joinQuery2.result)
  }

  def join3 : Future[Seq[(String, String)]] = {
    val joinQuery3 = for {(a,b) <- ownerDAO.owners join  reportDAO.reports on (_.owner_nickName === _.owner_nickName)}
      yield (a.owner_nickName, b.reportName)
    db.run(joinQuery3.result)
  }


  def join4(owner_nickName: String) : Future[Seq[(Int, String, String, String)]] = {

    val query = (for {(a, b) <- ownerDAO.owners join ownerRoleDAO.ownerRoles  on (_.owner_nickName === _.owner_nickName)
    } yield (a.id, a.owner_nickName,a.owner_realName, b.role)).filter(_._2 === owner_nickName)
    db.run(query.result)
  }

  def scheduledTask(): Future[Seq[(Int,String,String)]] = {
    val now = new DateTime()
    val query = (for {(a, b) <- tasklistDAO.tasklist join reportDAO.reports  on (_.report_id === _.id)}
                yield (a.taskid, a.owner_nickName,a.scheduled_execution_time,b.reportContent )).
                filter( x =>(x._3 > now.minusHours(1) && x._3 < now.plusHours(1))).
                map( x => (x._1, x._2, x._4))
    db.run(query.result)
  }

  // http://stackoverflow.com/questions/26816142/slick-query-with-multiple-joins-group-by-and-having  参考链接!!!
  def reportList (owner: String, pageNo:Int, pageSize:Int): (Future[Seq[(Int, String, String, String,DateTime,DateTime,Int,DateTime, Option[String] ,Option[DateTime])]],Future[Int]) = {
    val leftOuterJoin =(for {(t,r) <- tasklistDAO.tasklist joinRight reportDAO.reports on (_.report_id === _.id)}
      yield (r.id,r.owner_nickName,r.reportName,r.execute_type,r.once_scheduled_execute_time,
             r.circle_scheduled_start_time,r.circle_scheduled_interval_minutes, r.circle_scheduled_finish_time,
             t.map(_.reportfileName), t.map(_.execution_finish_time))).groupBy({
      case (k1, k2, k3, k4, k5, k6, k7, k8,v1,v2) => (k1, k2, k3, k4, k5, k6, k7, k8)
    }).map({ case (k, v) => (k._1,k._2,k._3,k._4,k._5,k._6,k._7,k._8, v.map(_._9).max,v.map(_._10).max )})

    val result = db.run(leftOuterJoin.result)
    val count = db.run(reportDAO.reports.filter(_.owner_nickName === owner).length.result)
    (result,count)
  }

}
