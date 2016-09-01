# User schema

# --- !Ups
create table `owner` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `owner_nickName` TEXT NOT NULL,
  `owner_realName` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `mobile` BIGINT NOT NULL,
  `email` TEXT NOT NULL,
  `memo` TEXT NOT NULL,
  `status` Boolean NOT NULL,
  `time` Datetime NOT NULL
)

# --- !Downs
drop table `identity`