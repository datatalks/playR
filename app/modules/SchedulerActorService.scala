package modules

import env.env

/**
 * Created by datatalks on 16/8/29.
 */
class SchedulerActorService {

  def circle( owner_nickName:String ,ReportContent: String , reportUrl: String  ) = {
    val fileName = reportUrl
    // 以下部分不论是前端提供,还是从数据库中获取都是同样的流程!!!
    val path = "MarkDown/reportR/RMD/" + fileName
    import scala.sys.process._
    (s"mkdir -p -- $path ").!   //  Make directory if it doesn't exist!
    scala.tools.nsc.io.File(path + "/" + fileName + ".Rmd").writeAll(ReportContent) // 删除了之前存在的内容!
    val dir = env.dir
    val Rfile_1delete_2append = dir + "/MarkDown/reportR/Rshell/" + fileName + ".R"
    (s"rm -f $Rfile_1delete_2append").!
    scala.io.Source.fromFile("reportR.R").getLines.
      foreach { line => scala.tools.nsc.io.File("MarkDown/reportR/Rshell/" + fileName + ".R").
        appendAll(line.replace("$fileR", fileName).replace("$dirR", dir) + sys.props("line.separator"))
      }
    import scala.sys.process._
    (s"R CMD BATCH MarkDown/reportR/Rshell/$fileName.R").!
    println("Report 被光荣的运行了一次!!!!!!")

  }
}
