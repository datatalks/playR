package models


import org.joda.time.DateTime



case class Rmd(id: Int, owner: String, reportR: String, execute_type: String, forward_execute_time: DateTime, circle_execute_interval_seconds:Int, modify_time: DateTime, url:String)

case class RmdFormData(owner: String, reportR: String)









