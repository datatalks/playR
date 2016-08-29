package modules

import javax.inject.Inject
import akka.actor.Actor
import javax.inject.Singleton
import play.Logger
import play.api.libs.json.{Json, JsValue}
import services.ReportDAO
import scala.concurrent.ExecutionContext.Implicits.global

//  备注： 其注入的 updater 这个类，只是一个相应的案例说明！案例中为空!!!!!!
@Singleton
class SchedulerActor @Inject() (reportDAO: ReportDAO, schedulerActorService: SchedulerActorService) extends Actor {
  def receive = {
    case "circle-scheduled-jobs" => circle_schedule()
    case "once-scheduled-jobs" => onceJOB()
  }
  def circle_schedule() = {

    reportDAO.getOwnerReport("xiaofan").map(
      res => {
        if (res.length == 0) {
          println("暂时没有循环的任务需要执行!!!!!!")
        }
        else {
          for (i <- res) yield  schedulerActorService.circle(i.owner_nickName, i.reportContent,i.random)
        }
      })

  }




  def circleJOB(): Unit ={
    Logger.info("circle-scheduled-jobs!!!!!!")
  }

  def onceJOB(): Unit ={
    Logger.info("once-scheduled-jobs")
  }
}
