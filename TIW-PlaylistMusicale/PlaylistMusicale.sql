CREATE DATABASE IF NOT EXISTS `PlaylistMusicale`;
USE `PlaylistMusicale`;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `nickname` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL
);

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;

-- Insert two users
INSERT INTO `User` (`nickname`, `password`) VALUES
('U1', 'password1'),
('U2', 'password2');

UNLOCK TABLES;

--
-- Table structure for table `Album`
--

DROP TABLE IF EXISTS `Album`;
CREATE TABLE `Album` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `image` varchar(256) NOT NULL,
  `interpreter` varchar(64) NOT NULL,
  `publication_year` int NOT NULL,
  `user_id` int NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `User`(`id`) ON UPDATE ON CASCADE ON DELETE NO ACTION
);

--
-- Dumping data for table `Album`
--

LOCK TABLES `Album` WRITE;

-- Insert albums
INSERT INTO `Album` (`title`, `image`, `interpreter`, `publication_year`, `user_id`) VALUES
('A1', 'A1.jpg', 'Michael Jackson', 1982, 1),
('A2', 'A2.jpg', 'Michael Jackson', 1982, 1),
('A3', 'A3.jpg', 'AC/DC', 1980, 2),
('A4', 'A4.jpg', 'Ed Sheeran', 2000, 2);

UNLOCK TABLES;

--
-- Table structure for table `Song`
--

DROP TABLE IF EXISTS `Song`;
CREATE TABLE `Song` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `genre` varchar(64) NOT NULL,
  `audio` varchar(256) NOT NULL,
  `album_id` int NOT NULL, 
  PRIMARY KEY (`id`),
  FOREIGN KEY (`album_id`) REFERENCES `Album`(`id`) ON UPDATE ON CASCADE ON DELETE NO ACTION
);

--
-- Dumping data for table `Song`
--

LOCK TABLES `Song` WRITE;

-- Insert songs
INSERT INTO `Song` (`title`,`genre`, `audio`, `album_id`) VALUES
('S1','Country', 'A1_S1.mp3', 1),
('S2','Rock', 'A1_S2.mp3', 1),
('S3','Pop', 'A2_S3.mp3', 2),
('S4','Pop', 'A3_S4.mp3', 3),
('S5','Classical', 'A3_S5.mp3', 3),
('S6','Classical', 'A3_S6.mp3', 3),
('S7','Classical', 'A3_S7.mp3', 3),
('S8','Classical', 'A3_S8.mp3', 3),
('S9','Classical', 'A3_S9.mp3', 3),
('S10','Classical', 'A4_S10.mp3', 4);
UNLOCK TABLES;

--
-- Table structure for table `Playlist`
--

DROP TABLE IF EXISTS `Playlist`;
CREATE TABLE `Playlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `publication_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, 
  `description` varchar(256) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `User`(`id`) ON UPDATE ON CASCADE ON DELETE NO ACTION
);

--
-- Dumping data for table `Playlist`
--

LOCK TABLES `Playlist` WRITE;

-- Insert two playlists
INSERT INTO `Playlist` (`title`, `publication_date`, `description`, `user_id`) VALUES
('P1', NOW(), 'A collection of classic 80s hits', 1),
('P2', NOW(), 'A playlist of epic rock songs', 1),
('P3', NOW(), 'A collection of popular songs', 2);

UNLOCK TABLES;

--
-- Table structure for table `SongPlaylist`
--
DROP TABLE IF EXISTS `SongPlaylist`;
CREATE TABLE `SongPlaylist` (
  `song_id` int NOT NULL,
  `playlist_id` int NOT NULL,
  `precedence` int NOT NULL DEFAULT 0,
  FOREIGN KEY (`song_id`) REFERENCES Song(`id`) ON UPDATE ON CASCADE ON DELETE NO ACTION,
  FOREIGN KEY (`playlist_id`) REFERENCES Playlist(`id`) ON UPDATE ON CASCADE ON DELETE NO ACTION,
  CONSTRAINT pk_song_playlist PRIMARY KEY (song_id, playlist_id)
);
--
-- Dumping data for table `SongPlaylist`
--

LOCK TABLES `SongPlaylist` WRITE;

-- Insert two rows in SongPlaylist table, linking the songs to the playlists
INSERT INTO SongPlaylist (song_id, playlist_id) VALUES
(1, 1), 
(1, 2), 
(2, 2),  
(4, 3),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(9, 3),
(10, 3);

UNLOCK TABLES;

