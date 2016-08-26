package services


object XmlFeedback{

  def textFeedback(openid:String, weixin:String, createTime:String, content: String)
      = <xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{createTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content>{content}</Content>
        </xml>

  def voiceFeedback(openid:String, weixin:String, CreateTime:String, Recognition: String )
      = <xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{CreateTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content>{Recognition}</Content>
        </xml>

  def newsFeedback(openid:String, weixin:String, CreateTime:String, title:String, Description:String, PicUrl:String, Url:String  )
      = <xml>
    <ToUserName>{openid}</ToUserName>
    <FromUserName>{weixin}</FromUserName>
    <CreateTime>{CreateTime}</CreateTime>
    <MsgType><![CDATA[news]]></MsgType>
    <ArticleCount>1</ArticleCount>
    <Articles>
      <item>
        <Title>{title}</Title>
        <Description>{Description}</Description>
        <PicUrl>{PicUrl}</PicUrl>
        <Url>{Url}</Url>
      </item>
    </Articles>
  </xml>


  def othersFeedback(openid:String, weixin:String, CreateTime:String )
      = <xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{CreateTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content><![CDATA[I CAN NOT follow u!!!]]></Content>
      </xml>


}