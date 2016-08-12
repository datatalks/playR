package modules


import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport   //   该 package 同  import play.libs.akka.AkkaGuiceSupport   冲突！！！



class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[SchedulerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}


