package modules

import javax.inject.Inject
import akka.actor.Actor
import javax.inject.Singleton
import play.Logger

//  备注： 其注入的 updater 这个类，只是一个相应的案例说明！案例中为空!!!!!!
@Singleton
class SchedulerActor @Inject() (updater: Updater) extends Actor {
  def receive = {
    case "circle-scheduled-jobs" => circleJOB()
    case "once-scheduled-jobs" => onceJOB()
  }
  var i = 0

  def circleJOB(): Unit ={
    i = i +1
    Logger.info(s"circle-scheduled-jobs!!!!!!.  $i")
  }

  def onceJOB(): Unit ={
    Logger.info("once-scheduled-jobs")
  }
}

class Updater{

}