package services

import com.github.stuxuhai.jpinyin._


object XmlVoiceInput{
  def chinese2pinyin(chars : String ) = PinyinHelper.convertToPinyinString(chars, ",", PinyinFormat.WITHOUT_TONE)

  def similarity(x:String , y: String) :Int  ={
                val xs = x.split(",")
                val ys = y.split(",")
                val pairs = for {
                  i <- xs
                  j <- ys
                if(i==j)} yield (i,j)
//                println(pairs.mkString)
                pairs.length}

  val subject = "注册/充值/投资/提现/赎回/客户/沉淀资金/转化率/营销/理财产品/绿能宝".split("/")

//  val index =  for( i <- 0 until subject.length ) yield (i)

  val pinyins = for(s <- subject) yield (chinese2pinyin(s))

  //  此时的 subject2pinyin 的数据结构是 tuple ( ( 系列一列表流水 )   (系列二列表流水)  )
  //  而非的 maxBy 需要的数据结构是 List( (_1,_2)  (_1,_2)  (_1,_2)   ), 故需要变换处理!!!!!!
  val subject2pinyin = { val pinyin = for(s <- subject) yield (chinese2pinyin(s))
                         (subject, pinyin)}

  val subject2pinyin2 = { val pinyin = for(s <- subject) yield (s, chinese2pinyin(s))
                              pinyin}
  //  此时的 subject2pinyin 的数据结构是 tuple ( ( 系列一列表流水 )   (系列二列表流水)  )
  //  而非的 maxBy 需要的数据结构是 List( (_1,_2)  (_1,_2)  (_1,_2)   ), 故需要变换处理!!!!!!
  def subject2similarity(input2pinyin:String) = { val sims = for(s <- subject2pinyin._2) yield similarity(input2pinyin,s)
                                                  val tempTuple =(subject2pinyin, sims)
                                                  val resultTuple = tempTuple._1._1  zip  tempTuple._1._2 zip tempTuple._2
                                                  val result = resultTuple.toList.maxBy(_._2)
    val response = ("根据您的发音,其同#"  + result._1._2 +  "#的相似指数为"
                     + result._2.toString + ".故,我们将为你呈现#" + result._1._1 + "#相关的数据报告......")

    val response2ISO_8859_1  = new String(response.getBytes("UTF-8") , "ISO-8859-1")
    val feedback = if( result._2 == 0) "Sorry, I didn't follow u,  Pls. Speak Again!" else response2ISO_8859_1
    feedback}

  def subject2similarity2(input2pinyin:String) = { val sims = for(s <- subject2pinyin._2) yield List( s, similarity(input2pinyin,s))
                                                      sims}
}
