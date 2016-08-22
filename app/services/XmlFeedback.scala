package services


object XmlFeedback{

  def textFeedback(openid:String, weixin:String, CreateTime:String)
      = <xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{CreateTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content><![CDATA[thanks for the message!!!]]></Content>
        </xml>

  def voiceFeedback(openid:String, weixin:String, CreateTime:String, Recognition: String )
      = <xml>
            <ToUserName>{openid}</ToUserName>
            <FromUserName>{weixin}</FromUserName>
            <CreateTime>{CreateTime}</CreateTime>
            <MsgType>text</MsgType>
            <Content>{Recognition}</Content>
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