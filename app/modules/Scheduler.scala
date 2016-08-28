package modules

import javax.inject.{Named, Inject}
import akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._



class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0 seconds, 333 seconds, schedulerActor, "circle-scheduled-jobs")
  system.scheduler.schedule(
    1 minutes, 333 seconds, schedulerActor, "once-scheduled-jobs")
}
