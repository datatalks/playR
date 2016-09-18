# User schema

# --- !Ups
create table `tasklist` (
  `taskid` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `report_id` INT NOT NULL,
  `owner_nickName` TEXT NOT NULL,
  `reportContent` TEXT NOT NULL,
  `reportURL` TEXT NOT NULL,
  `scheduled_time` Datetime NOT NULL,
  `executed_start_time` Datetime NOT NULL,
  `executed_finish_time` Datetime NOT NULL
)

# --- !Downs
drop table `tasklist`
