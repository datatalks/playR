package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Report, Owner}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class JoinDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, ownerDAO:OwnerDAO, reportDAO:ReportDAO) extends HasDatabaseConfigProvider[JdbcProfile] {
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



}