package services

import com.github.stuxuhai.jpinyin._
import play.api.libs.json._
import play.Logger
object XmlVoiceInput2 {

  val json: JsValue = Json.obj(
    "data" -> Json.arr(
      Json.obj(
        "news_subject" -> "注册",
        "news_title" -> "关于注册的分析模板",
        "news_Description" -> "注册分析的思路与方法",
        "news_PicUrl" -> "http://www.itisbi.com/static/image/common/logo.png",
        "news_Url" -> "http://playr.data-talks.com/assets/htmls/index.html"
      ),
      Json.obj(
        "news_subject" -> "充值",
        "news_title" -> "关于充值的分析模板",
        "news_Description" -> "充值分析的思路与方法",
        "news_PicUrl" -> "http://www.itisbi.com/static/image/common/logo.png",
        "news_Url" -> "http://playr.data-talks.com/assets/htmls/index.html"
      ),
      Json.obj(
        "news_subject" -> "投资",
        "news_title" -> "关于投资的分析模板",
        "news_Description" -> "投资分析的思路与方法",
        "news_PicUrl" -> "http://www.itisbi.com/static/image/common/logo.png",
        "news_Url" -> "http://playr.data-talks.com/assets/htmls/index.html"
      ),
      Json.obj(
        "news_subject" -> "提现",
        "news_title" -> "关于提现的分析模板",
        "news_Description" -> "提现分析的思路与方法",
        "news_PicUrl" -> "http://www.itisbi.com/static/image/common/logo.png",
        "news_Url" -> "http://playr.data-talks.com/assets/htmls/index.html"
      )
    )
  )

  def chinese2pinyin(chars: String) = PinyinHelper.convertToPinyinString(chars, ",", PinyinFormat.WITHOUT_TONE)

  def similarity(x: String, y: String): Int = {
    val xs = x.split(",")
    val ys = y.split(",")
    val pairs = for {
      i <- xs
      j <- ys
      if (i == j)} yield (i, j)
    //                println(pairs.mkString)
    pairs.length
  }

  val news_subject = (json \\ "news_subject").map(_.as[String])
  val news_title = (json \\ "news_title").map(_.as[String])
  val news_Description = (json \\ "news_Description").map(_.as[String])
  val news_PicUrl = (json \\ "news_PicUrl").map(_.as[String])
  val news_Url = (json \\ "news_Url").map(_.as[String])


  val pinyins = for (s <- news_subject) yield (chinese2pinyin(s))

  //  此时的 subject2pinyin 的数据结构是 tuple ( ( 系列一列表流水 )   (系列二列表流水)  )
  //  而非的 maxBy 需要的数据结构是 List( (_1,_2)  (_1,_2)  (_1,_2)   ), 故需要变换处理!!!!!!
  val subject2pinyin = {
    val pinyin = for (s <- news_subject) yield (chinese2pinyin(s))
    (news_subject, pinyin)
  }

  //  此时的 subject2pinyin 的数据结构是 tuple ( ( 系列一列表流水 )   (系列二列表流水)  )
  //  而非的 maxBy 需要的数据结构是 List( (_1,_2)  (_1,_2)  (_1,_2)   ), 故需要变换处理!!!!!!
  def subject2similarity(input2pinyin: String) = {
    val sims = for (s <- subject2pinyin._2) yield similarity(input2pinyin, s)
    val tempTuple = (sims zip news_subject zip news_title zip news_Description zip news_PicUrl zip news_Url)
    val result = tempTuple.toList.maxBy(_._1)

    val response = (  result._1._1._1._1._2,result._1._1._1._1._1, result._1._1._1._2, result._1._1._2, result._1._2 , result._2  )
    val response2Error = ( "未找到相应的主题模板", 0,  "暂未设置您提到的问题", "请直接根据以下链接进行自助分析", "http://www.itisbi.com/static/image/common/logo.png" , "http://playr.data-talks.com/assets/htmls/index.html" )
    val feedback = if( response._2 == 0) response2Error else response  // 字符集满足本地测试使用!!!
    val feedback2ISO_8859_1 = (new String(feedback._1.getBytes("UTF-8") , "ISO-8859-1"), feedback._2, new String(feedback._3.getBytes("UTF-8") , "ISO-8859-1"), new String(feedback._4.getBytes("UTF-8") , "ISO-8859-1"), feedback._5, feedback._6)

    Logger.info("feedback2ISO_8859_1 ===========" + feedback2ISO_8859_1)
    feedback2ISO_8859_1
  }
}
