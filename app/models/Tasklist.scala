package models

import org.joda.time.DateTime

case class Tasklist(taskid: Int, report_id:Int, owner_nickName: String,  reportContent: String, reportURL: String,
                    scheduled_time: DateTime, executed_start_time: DateTime, executed_finish_time:DateTime)








