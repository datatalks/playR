package models


import org.joda.time.DateTime



case class Report(id: Int, owner: String, reportName: String,  reportContent: String, execute_type: String, forward_execute_time: DateTime, circle_execute_interval_seconds:Int, modify_time: DateTime, reportUrl:String)

case class ReportFormData(owner: String, reportName: String, reportContent: String)








