package models

import org.joda.time.DateTime

case class Tasklist(taskid: Int, report_id:Int, owner_nickName: String,reportContent :String,
                    scheduled_execution_time: DateTime, executed_start_time: DateTime, executed_finish_time:DateTime)








