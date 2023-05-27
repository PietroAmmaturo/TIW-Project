CREATE TABLE `Album` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `image` varchar(256) NOT NULL,
  `interpreter` varchar(64) NOT NULL,
  `publication_year` int NOT NULL,
  `user_id` int NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `User`(`id`)
);