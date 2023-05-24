CREATE TABLE `SongPlaylist` (
  `song_id` int NOT NULL,
  `playlist_id` int NOT NULL,
  FOREIGN KEY (`song_id`) REFERENCES Song(`id`),
  FOREIGN KEY (`playlist_id`) REFERENCES Playlist(`id`),
  CONSTRAINT pk_song_playlist PRIMARY KEY (song_id, playlist_id)
);