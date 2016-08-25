package services

import javax.inject.Inject

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Report, Identity}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class JoinDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, identityDAO:IdentityDAO, reportDAO:ReportDAO) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  def join1 : Future[Seq[(Identity, Report)]] = {
    val joinQuery1 = identityDAO.identitys.join(reportDAO.reports).on(_.id === _.id)
    db.run(joinQuery1.result)
  }

  def join2 : Future[Seq[(String, String)]] = {
    val joinQuery2 = for {(a,b) <- identityDAO.identitys join  reportDAO.reports on (_.id === _.id)}
                     yield (a.identity, b.reportName)
    db.run(joinQuery2.result)
  }

  def join3 : Future[Seq[(String, String)]] = {
    val joinQuery3 = for {(a,b) <- identityDAO.identitys join  reportDAO.reports on (_.id === _.id)}
      yield (a.identity, b.reportName)
    db.run(joinQuery3.result)
  }



}