# User schema

# --- !Ups
create table `identityRole` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `identity` TEXT NOT NULL,
  `role` TEXT NOT NULL,
  `memo` TEXT NOT NULL,
  `time` Datetime NOT NULL
)

# --- !Downs
drop table `identity`