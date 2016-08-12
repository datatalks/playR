package modules

import javax.inject.{Named, Inject}

import
import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.AbstractModule
import com.oracle.deploy.update.Updater
import play.libs.akka.AkkaGuiceSupport

import scala.concurrent.ExecutionContext

class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[SchedulerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}

class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0.microseconds, 1.minutes, schedulerActor, "update")
  system.scheduler.schedule(
    30.minutes, 30.days, schedulerActor, "clean")
}


@Singleton
class SchedulerActor @Inject() (updater: Updater) extends Actor {
  def receive = {
    case "update" => updateDB()
    case "clean" => clean()
  }

  def updateDB(): Unit ={
    Logger.debug("updates running")
  }

  def clean(): Unit ={
    Logger.debug("cleanup running")
  }
}