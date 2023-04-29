CREATE TABLE `Playlist` (
  'id' int NOT NULL AUTO_INCREMENT,
  'title' varchar(64) NOT NULL,
  'file_url' varchar(256) NOT NULL,
  'publication_date' datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, 
  'description' varchar(150) NOT NULL,
  'user_id' int NOT NULL
  PRIMARY KEY ('id'),
  FOREIGN KEY ('user_id') REFERENCES User('id')
)