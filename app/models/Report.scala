package models


import org.joda.time.DateTime

case class Report(id: Int, owner_nickName:String, reportName: String,  reportContent: String, execute_type: String,
                  once_scheduled_execute_time: DateTime,
                  circle_execute_interval_seconds:Int, circle_next_scheduled_execute_time: DateTime,
                  once2circle_last_executed_time: DateTime,
                  modify_time: DateTime, reportUrl:String, random:String, status :Int)

case class ReportFormData(owner_nickName: String, reportName: String, reportContent: String)








