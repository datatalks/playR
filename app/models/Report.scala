package models


import org.joda.time.DateTime

case class Report(id: Int, owner_nickName:String, reportName: String,  reportContent: String, execute_type: String,
                  once_scheduled_execute_time: DateTime,
                  circle_scheduled_start_time:DateTime, circle_scheduled_interval_minutes:Int, circle_scheduled_finish_time:DateTime,
                  modify_time: DateTime, status :Int)

case class ReportFormData(owner_nickName: String, reportName: String, reportContent: String)





