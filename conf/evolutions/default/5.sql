# User schema

# --- !Ups
create table `tasklist` (
  `taskid` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `report_id` INT NOT NULL,
  `owner_nickName` TEXT NOT NULL,
  `report_filename` TEXT NOT NULL,
  `reportContent` TEXT NOT NULL,
  `scheduled_execution_time` Datetime NOT NULL,
  `execution_start_time` Datetime NOT NULL,
  `execution_finish_time` Datetime NOT NULL
)

# --- !Downs
drop table `tasklist`
