class SongManager {
    constructor() {
        this.mainElement = document.querySelector('#playerMain');

        this.goToSong = function(id) {
			while(this.mainElement.firstChild) {
				this.mainElement.removeChild(this.mainElement.firstChild);
			}
            for (let i = 0; i < this.songs.length; i++) {
                if (id == this.songs[i][0].id) {
					this.renderSong(this.mainElement, this.songs[i]);
                }
            }
        };
		
		this.update = function(playlistSongsWithAlbum) {		
			this.songs = playlistSongsWithAlbum;
		};
		
		this.renderSong = function(parent, entry) {
		        const songId = entry[0].id;
		        const songTitle = entry[0].title;
		        const songGenre = entry[0].genre;
		        const songAudio = entry[0].audio;
		        const imageSrc = entry[1].image;
		        const imageAlt = entry[1].title;
		
		        const playerSong = document.createElement('div');
		        playerSong.classList.add('card');
		        playerSong.classList.add('thick');
				playerSong.setAttribute('data-id', songId);

				const title = document.createElement('h2');
		        title.innerHTML = `${songTitle}`;
		        playerSong.appendChild(title);
		        
		        const image = document.createElement('img');
		        image.src = contextPath + `FileHandler?fileName=${imageSrc}`;
		        image.alt = imageAlt;
		        playerSong.appendChild(image);
		
		        const artist = document.createElement('p');
		        artist.innerHTML = `Artist: <span>${entry[1].interpreter}</span>`;
		        playerSong.appendChild(artist);
		
		        const album = document.createElement('p');
		        album.innerHTML = `Album: <span>${entry[1].title}</span>`;
		        playerSong.appendChild(album);
		
		        const albumYear = document.createElement('p');
		        albumYear.innerHTML = `Album Year: <span>${entry[1].publicationYear}</span>`;
		        playerSong.appendChild(albumYear);
		
		        const genre = document.createElement('p');
		        genre.innerHTML = `Genre: <span>${songGenre}</span>`;
		        playerSong.appendChild(genre);
		
				const audio = document.createElement('audio');
				audio.setAttribute('controls', '');
				audio.src = contextPath + `FileHandler?fileName=${songAudio}`;
		        playerSong.appendChild(audio);
		        
		        parent.appendChild(playerSong);
		}
    }
    
    
}