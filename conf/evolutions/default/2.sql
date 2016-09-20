# User schema

# --- !Ups
create table `report` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `owner_nickName` TEXT NOT NULL,
  `reportName` TEXT NOT NULL,
  `reportContent` TEXT NOT NULL,
  `execute_type` TEXT NOT NULL,
  `once_scheduled_execute_time` Datetime ,
  `circle_scheduled_start_time` Datetime ,
  `circle_scheduled_interval_minutes` INT ,
  `circle_scheduled_finish_time` Datetime ,
  `modify_time` Datetime NOT NULL,
  `status` INT NOT NULL
)

# --- !Downs
drop table `report`
