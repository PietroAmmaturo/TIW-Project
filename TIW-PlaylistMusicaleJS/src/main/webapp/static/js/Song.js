class SongManager {
    constructor() {
		this.pageSize = 1;
        this.pageNumber = parseInt(urlParams.get('songPage')) || 1;
        this.songs = document.getElementsByClassName('playerSong');

        this.updateQueryParams = function() {
            const updatedUrlParams = new URLSearchParams(window.location.search);
            updatedUrlParams.set('songPage', this.pageNumber.toString());

            const newUrl = window.location.pathname + '?' + updatedUrlParams.toString();
            window.history.pushState({ path: newUrl }, '', newUrl);
        };

        this.hideAndShow = function() {
            for (let i = 0; i < this.songs.length; i++) {
                if (i < this.pageNumber - this.pageSize || i >= this.pageNumber) {
                    this.songs[i].hidden = true;
                } else {
                    this.songs[i].hidden = false;
                }
            }
        };

		this.getSongPageNumber = function(songId) {
			for (let i = 0; i < this.songs.length; i++) {
                const song = this.songs[i];
                const id = song.getAttribute("data-id");
                console.log("going to song", songId, song, id);
                if (id == songId) {
					return Math.floor(i / this.pageSize) + 1;
                }
            }
            return null;
		};
		
		this.setPageNumber = function(pageNumber) {
			this.pageNumber = pageNumber;
		};
		
		this.update = function(playlistSongsWithAlbum) {		
		    const playerContainer = document.getElementById('playerContainer');
		    playerContainer.innerHTML = '';
		
		    playlistSongsWithAlbum.forEach((entry) => {
		        const songId = entry[0].id;
		        const songTitle = entry[0].title;
		        const songGenre = entry[0].genre;
		        const songAudio = entry[0].audio;
		        const imageSrc = entry[1].image;
		        const imageAlt = entry[1].title;
		
		        const playerSong = document.createElement('div');
		        playerSong.classList.add('playerSong');
				playerSong.setAttribute('data-id', songId);
				
		        const image = document.createElement('img');
		        image.src = contextPath + `FileHandler?fileName=${imageSrc}`;
		        image.alt = imageAlt;
		        playerSong.appendChild(image);
		
		        const title = document.createElement('p');
		        title.innerHTML = `Title: <span>${songTitle}</span>`;
		        playerSong.appendChild(title);
		
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
				audio.setAttribute('preload', 'none');
				audio.setAttribute('controls', '');
				audio.src = contextPath + `FileHandler?fileName=${songAudio}`;
		        playerSong.appendChild(audio);
		        
		        playerContainer.appendChild(playerSong);
		        
		        this.songs = document.getElementsByClassName('playerSong');
		        this.setPageNumber(1);
		        this.updateQueryParams();
		        this.hideAndShow();
		    });
		};
    }
}