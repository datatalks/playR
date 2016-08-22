package services

import javax.inject.Inject

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val users = TableQuery[UserTableDef]


  def addUser(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def deleteUser(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }

  def getUser(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def listAllUsers: Future[Seq[User]] = {
    db.run(users.result)
  }

  def listAllUsersforTestig: Future[Seq[String]] = {
    db.run(users.map(_.email).result)
  }


  private class UserTableDef(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def mobile = column[Long]("mobile")
    def email = column[String]("email")

    override def * =
      (id, firstName, lastName, mobile, email) <>(User.tupled, User.unapply _)
  }
}