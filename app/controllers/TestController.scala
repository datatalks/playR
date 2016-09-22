package controllers

import javax.inject.Inject
import com.github.stuxuhai.jpinyin.{PinyinFormat, PinyinHelper}
import env.env
import models.{Tasklist}
import org.joda.time.{Minutes, DateTime}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.ws.WSClient
import play.api.mvc._
import security.Cipher
import services._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import play.api.Logger
import play.api.libs.json._
import scala.util.Try


class TestController   @Inject() (tasklistDAO:TasklistDAO, reportDAO:ReportDAO,  joinDAO: JoinDAO, ownerRoleDAO: OwnerRoleDAO,ws:WSClient) extends Controller {

  def task() = Action.async { implicit request =>
    val reports =  Await.result(reportDAO.scheduleReport, Duration.Inf)
    val iniTime = new DateTime("2014-09-01T0:0:0.0+08:00")
    for (i <- reports) {
      val now = new DateTime()
      val from = math.ceil(Minutes.minutesBetween(i._6,now.minusHours(1)).getMinutes().toDouble / i._7).toInt
      val to = math.floor(Minutes.minutesBetween(i._6,now.plusHours(1)).getMinutes().toDouble / i._7).toInt
      val ts = for( j <- List.range(from, to+1)) yield (i._6.plusMinutes(j * i._7 ))
      if(i._4 == "once" && i._5.isAfter(now.minusHours(1)) && i._5.isBefore(now.plusHours(1)) && !Await.result(tasklistDAO.exists(i._1,i._5), Duration.Inf) ){
        val t = Tasklist(0,i._1,i._2, i._5,iniTime,iniTime)
        tasklistDAO.addTask(t)}
      else if(i._4=="circle" && ts.length > 0 ){
        for(j <- ts){ if( !Await.result(tasklistDAO.exists(i._1,j), Duration.Inf) ){
          val t = Tasklist(0,i._1,i._2, j,iniTime,iniTime)
          tasklistDAO.addTask(t)}}}}
    Future.successful(Ok( reports.toString ))
  }

  def taskkk() = Action.async { implicit request =>
      joinDAO.scheduledTask().map( data =>
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
                                           tasklistDAO.upadte_finish_time(i._1, new DateTime())
                                         }})
    Future.successful(Ok( "task to DO!" ))
  }


  def pinyin()= Action.async { implicit request =>
    val res1 = PinyinHelper.convertToPinyinString("李.成. 竹。。。 ", ",", PinyinFormat.WITHOUT_TONE)
    val res2 = res1.replaceAll(",",  "")
    val res3 = res1.split(",")
    Future.successful(  Ok( res1 )  )
  }

  def pinyinn()= Action.async { implicit request =>
    val res1 = PinyinHelper.convertToPinyinString("李.成. 竹。。。 ", "$", PinyinFormat.WITHOUT_TONE)
    val res2 = res1.replaceAll(",",  "")
    val res3 = res1.split(",")
    Future.successful(  Ok( res1 )  )
  }

  def getsessionvalue() = Action.async { implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    val rerere = Cipher(session_owner_nickName).decryptWith("playR")
    val res = Await.result(ownerRoleDAO.getOwnerRole(rerere), Duration.Inf)
    Future.successful(  Ok("session ===" + res )  )
  }


  def innerJoin1 =  Action.async { implicit request =>

    val res = joinDAO.join1
    for( i <- res) println("XXXXX  is" + i)
    Future.successful(Ok(res.toString))
  }

  def innerJoin2 =  Action.async { implicit request =>
    val res = joinDAO.join2
    for( i <- res) println(i)

    println("the res is " + res.toString)

    val result: Try[Seq[(String, String)]] = Await.ready(res, Duration.Inf).value.get
    Future.successful(Ok(result.toString))
  }

  //  对于表关联中使用异步Future得到的结果，通过两个map实现了异步结果的HTTP response!
  //  备注，对于常见的 Future 类型的结果返回错误如下：
  // Cannot write an instance of Seq[(String, String)] to HTTP response. Try to define a Writeable[Seq[(String, String)]]
  def innerJoin3 =  Action.async { implicit request =>
      joinDAO.join3 map { res =>
        val t = ";"
        Ok(   res.map( x => x._1 + t + x._2 ).mkString   )
      }
  }

  def innerJoin4 =  Action.async {implicit request =>
    val session_owner_nickName = request.session.get("owner_nickName").mkString
    val result  = Await.result(joinDAO.join4(Cipher(session_owner_nickName).decryptWith("playR")), Duration.Inf)
    val output = result groupBy(data => (data._1, data._2, data._3)) map {
        case (k, v) => (k, v map {case (k1, k2, k3, v) => v} )}
    val finals = for(data <- output.toList) yield (data._1._1,data._1._2,data._1._3,data._2.reduceLeft(_+"&"+_))
    println("finals====" + finals.toString )
    val json: JsValue = Json.obj(
              "data" -> Json.obj("ownerid" -> JsNumber(finals(0)._1),
                                  "owner_nickName" -> JsString(finals(0)._2),
                                  "owner_realName" -> JsString(finals(0)._3),
                                  "role" -> finals(0)._4.toString.split("&")     ),
              "message" -> "获取成功")
    Future.successful(Ok(json))}


  def json3 = Action {
    val name = "我是李爬爬"
    val json1: JsValue = Json.obj(
      "name" -> name,
      "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
      "residents" -> Json.arr(
        Json.obj(
          "name" -> "Fiver",
          "age" -> 4,
          "role" -> JsNull
        ),
        Json.obj(
          "name" -> "Bigwig",
          "age" -> 6,
          "role" -> "Owsla"
        )
      )
    )
    val url = "http://XXXX"
    val json2: JsValue = Json.obj(
      "data" -> url,
      "message" -> "提交成功咯~~~"
    )
    val json3: JsValue = Json.obj(
      "data" -> Json.arr(
        Json.obj(
          "title" -> "报告名称",
          "date" -> "生成报告日期",
          "url" -> "生成报告地址"
        )
      ),
      "message" -> "提交成功咯~~~"
    )
    Ok(json3)
  }



  def r1 = Action.async { implicit request =>
    import scala.sys.process._
    "touch XXX.txt".!
    val r: String = "$a " + "papapap啪啪啪啪啪"
    println("r is : " + r)
    // MAC OS sed 命令使用 VS Linux sed 命令的使用存在相应的差异和区别：
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  r , "XXX.txt").!
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  "$a XX777X", "/Users/datatalks/Desktop/xiaofan.txt").!
    //Seq( "/opt/local/libexec/gnubin/sed",  "-i",  r, "/Users/datatalks/Desktop/xiaofanR.txt").!
    "R CMD BATCH R.R".!
    println("R.R Script run successfully！！！")
    println("succeeded!!!")
    Future.successful(Ok("This is the Test!!!" + r))
  }


  def r2 = Action.async { implicit request =>
    Logger.info("Application startup...")
    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")


    //    import laika.api._
    //    import laika.parse.markdown._
    //    import laika.render._
    //    import laika.parse._
    //
    //    import laika.api.Parse
    //    import laika.api.Render
    //    import laika._
    //    import laika.factory._
    //
    //    Transform from Markdown to laika.render.HTML fromFile "MarkDown/preview1.md" toFile "MarkDown/preview1.html"

    // Future.successful(Ok("This is the Test for R script!!!"))
    // Future.successful(Redirect("http://stackoverflow.com/questions/10962694"))

    Future.successful(Redirect("http://localhost:88/preview1.html"))
  }


  def r3 = Action.async {
    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")


    ws.url("http://localhost:88/preview1.html").get().map { response =>
      Ok(new String(response.body.getBytes("ISO-8859-1"), response.header(CONTENT_ENCODING).getOrElse("UTF-8"))).as(HTML)
    }
  }

  def r4 = Action.async {

    import scala.sys.process._
    "R CMD BATCH R.R".!
    println("preview1.md Created successfully！！！")

    ws.url("http://localhost:88/preview1.html").get().map { response =>

      def responseBody = response.header(CONTENT_TYPE).filter(_.toLowerCase.contains("charset")).fold(new String(response.body.getBytes("ISO-8859-1"), "UTF-8"))(_ => response.body)
      val result = responseBody.toString
      Ok(result).as("text/html")
    }
  }



  def setsessions () = Action.async { implicit request =>
    Future.successful(  Ok(" sessions are setted or updated!").withSession(
      request.session + ("identity" -> "YYY8888BBBBBBBBBBBYYY"))  )
  }

  def getsessions () = Action.async { implicit request =>
    Future.successful(  request.session.get("roles").map { data =>
      println(" Cipher is ====== "+data)
      Ok("Hello " +  "Cipher" )
    }.getOrElse {Unauthorized("Oops, you are not connected")}  )
  }

  def rmsessions () = Action.async { implicit request =>

    val message:String = "1"
    val key:String = "Key"

    val encrypted = Cipher(message).simpleOffset(5)
    println(encrypted)
    val decrypted = Cipher(encrypted).simpleOffset(-5)
    println(decrypted)

    val encrypted2 = Cipher(message).encryptWith("XXXXXX")
    println(encrypted2)
    val decrypted2 = Cipher(encrypted2).decryptWith(key)
    println(decrypted2)

    Future.successful(  Ok("Bye").withNewSession )
  }


}










