create table Album{
'id' int PRIMARY KEY references Song('id_album')
'title' varchar(64) NOT NULL
'image' ??? NOT NULL
'interpreter' varchar(64) NOT NULL
'publication_year' int NOT NULL
}