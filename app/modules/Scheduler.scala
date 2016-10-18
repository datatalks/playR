package modules

import javax.inject.{Named, Inject}
import akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._



class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    3 seconds, 300 seconds, schedulerActor, "report2task")
  system.scheduler.schedule(
    7 seconds, 360 seconds, schedulerActor, "task2execution")
}
