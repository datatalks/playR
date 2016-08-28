package modules


import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport   //   该 package 同import play.libs.akka.AkkaGuiceSupport冲突！！！


class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[SchedulerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}


