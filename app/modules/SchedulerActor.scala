package modules

import javax.inject.Inject
import akka.actor.Actor
import javax.inject.Singleton
import env.env
import models.Tasklist
import org.joda.time.{Minutes, DateTime}
import play.Logger
import play.api.libs.json.{Json, JsValue}
import services.{TasklistDAO, ReportDAO}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

//  备注： 其注入的 updater 这个类，只是一个相应的案例说明！案例中为空!!!!!!
@Singleton
class SchedulerActor @Inject() (reportDAO: ReportDAO, tasklistDAO:TasklistDAO, schedulerActorService: SchedulerActorService) extends Actor {
  def receive = {
    case "report2task" => report2task()
    case "task2execution" => task2execution()
  }

  def report2task(): Unit ={
    Logger.info("report2task......")
    val reports =  Await.result(reportDAO.scheduleReport, Duration.Inf)
    val iniTime = new DateTime("2014-09-01T0:0:0.0+08:00")
    for (i <- reports) {
      val now = new DateTime()
      val report_filename = i._2 + "_Report_" +  scala.util.Random.alphanumeric.take(10).mkString
      val from = math.ceil(Minutes.minutesBetween(i._7,now.minusHours(1)).getMinutes().toDouble / i._8).toInt
      val to = math.floor(Minutes.minutesBetween(i._7,now.plusHours(1)).getMinutes().toDouble / i._8).toInt
      val ts = for( j <- List.range(from, to+1)) yield (i._7.plusMinutes(j * i._8 ))
      if(i._5 == "once" && i._6.isAfter(now.minusHours(1)) && i._6.isBefore(now.plusHours(1)) && !Await.result(tasklistDAO.exists(i._1,i._6), Duration.Inf) ){
        val t = Tasklist(0,i._1,i._2, i._4,  i._6,iniTime,iniTime)
        tasklistDAO.addTask(t)}
      else if(i._5=="circle" && ts.length > 0 ){
        for(j <- ts){ if( !Await.result(tasklistDAO.exists(i._1,j), Duration.Inf) ){
          val t = Tasklist(0,i._1,i._2,i._4, j,iniTime,iniTime)
          tasklistDAO.addTask(t)}}}}
  }

  def task2execution(): Unit ={
    Logger.info("task2execution is ......")
    tasklistDAO.scheduledTask().map( data =>
                { for(i <- data) {
                      val fileName = i._2 +  "_ReportTask_" + i._1.toString
                      val ReportContent = i._3
                      println("fffilename===" + fileName)
                      val path = "MarkDown/reportR/RMD/" + fileName
                      import scala.sys.process._
                      (s"mkdir -p -- $path ").!   //  Make directory if it doesn't exist!
                      scala.tools.nsc.io.File(path + "/" + fileName + ".Rmd").writeAll(ReportContent) // 删除了之前存在的内容!
                      val dir = env.dir
                      val Rfile_1delete_2append = dir + "/MarkDown/reportR/Rshell/" + fileName + ".R"
                      // 对于可能重复执行的报告模板,删除之前相应的.R文件
                      (s"rm -f $Rfile_1delete_2append").!
                      scala.io.Source.fromFile("reportR.R").getLines.
                        foreach { line => scala.tools.nsc.io.File("MarkDown/reportR/Rshell/" + fileName + ".R").
                          appendAll(line.replace("$fileR", fileName).replace("$dirR", dir) + sys.props("line.separator"))}
                      val HTML_folder_delete_for_update = dir + "/MarkDown/reportR/RMD/" + fileName + "/figure"
                      // 对于可能重复执行的报告模板,删除之前相应的 HTML 文件夹与文件
                      (s"rm -rf $HTML_folder_delete_for_update").!
                      tasklistDAO.upadte_start_time(i._1, new DateTime())
                      // 执行 RMD 对应的文件生成相应的 HTML 文件夹与文件
                      (s"R CMD BATCH MarkDown/reportR/Rshell/$fileName.R").!
                      tasklistDAO.upadte_finish_time(i._1, new DateTime())}})
  }
}
