# User schema

# --- !Ups
create table `rmd` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `owner` TEXT NOT NULL,
  `reportR` TEXT NOT NULL,
  `execute_type` TEXT NOT NULL,
  `forward_execute_time` Date NOT NULL,
  `circle_execute_interval_seconds` INT NOT NULL,
  `modify_time` Datetime NOT NULL,
  `url` TEXT NOT NULL
)

# --- !Downs
drop table `rmd`
