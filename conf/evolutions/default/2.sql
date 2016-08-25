# User schema

# --- !Ups
create table `report` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `owner` TEXT NOT NULL,
  `reportName` TEXT NOT NULL,
  `reportContent` TEXT NOT NULL,
  `execute_type` TEXT NOT NULL,
  `forward_execute_time` Date NOT NULL,
  `circle_execute_interval_seconds` INT NOT NULL,
  `modify_time` Datetime NOT NULL,
  `reportUrl` TEXT NOT NULL
)

# --- !Downs
drop table `report`
