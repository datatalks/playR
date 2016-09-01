# User schema

# --- !Ups
create table `ownerRole` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `owner_nickName` TEXT NOT NULL,
  `role` TEXT NOT NULL,
  `memo` TEXT NOT NULL,
  `status` Boolean NOT NULL,
  `time` Datetime NOT NULL
)

# --- !Downs
drop table `ownerRole`