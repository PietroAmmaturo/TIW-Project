CREATE TABLE `Song` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `audio` varchar(256) NOT NULL,
  `album_id` int NOT NULL, 
  PRIMARY KEY (`id`),
  FOREIGN KEY (`album_id`) REFERENCES `Album`(`id`)
);