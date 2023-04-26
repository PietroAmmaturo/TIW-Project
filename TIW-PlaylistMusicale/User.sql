create table User{
'id' int PRIMARY KEY references Playlist('id_creator')
'nickanme' varchar(45) NOT NULL
'password' varchar(64) NOT NULL
}