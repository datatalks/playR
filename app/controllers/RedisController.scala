package controllers

import javax.inject.Inject
import org.sedis.Pool
import play.api.mvc._
import scala.concurrent.Future

class RedisController @Inject()(sedisPool: Pool) extends Controller {

  def redisget(key:String) =  Action.async { implicit request =>
    val getValue: String = sedisPool.withJedisClient(client => client.get(key))
    Future.successful(Ok(getValue))
  }

  def redisset(key:String,value:String) =  Action.async { implicit request =>
    sedisPool.withJedisClient(client => client.set(key,value))
    Future.successful(Ok("Success!"))
  }


  def redis(key:String) =  Action.async { implicit request =>
    val getValue = sedisPool.withJedisClient(client => client.hgetAll(key))
    println(getValue.size())
    println(getValue.get("HASH-key-XXXXXXXXXXXXr#z#h#k#p"))

    val getmap = sedisPool.withJedisClient(client => client.hget(key, "HASH-key-XXXXXXXXXXXXr#z#h#k#p"))

    println(getmap)

    Future.successful(Ok(getValue.toString))
  }

}
