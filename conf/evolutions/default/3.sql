# User schema

# --- !Ups
create table `identity` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `identity` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `memo` TEXT NOT NULL,
  `status` Boolean NOT NULL,
  `time` Datetime NOT NULL
)

# --- !Downs
drop table `identity`