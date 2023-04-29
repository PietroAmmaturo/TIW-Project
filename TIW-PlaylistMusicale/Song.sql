CREATE TABLE `Song` (
  'id' int NOT NULL AUTO_INCREMENT,
  'title' varchar(64) NOT NULL,
  'album_id' int NOT NULL, 
  'playlist_id' int,
  'user_id' int NOT NULL
  PRIMARY KEY ('id'),
  FOREIGN KEY ('album_id') REFERENCES Album('id')
  FOREIGN KEY ('playlist_id') REFERENCES Playlist('id')
  FOREIGN KEY ('user_id') REFERENCES User('id')
)